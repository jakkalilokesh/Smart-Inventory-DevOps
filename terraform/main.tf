# Terraform configuration for SmartInventory infrastructure

terraform {
  required_version = ">= 1.0"
  required_providers {
    aws = {
      source  = "hashicorp/aws"
      version = "~> 5.0"
    }
    kubernetes = {
      source  = "hashicorp/kubernetes"
      version = "~> 2.23"
    }
    helm = {
      source  = "hashicorp/helm"
      version = "~> 2.12"
    }
  }
}

provider "aws" {
  region = var.aws_region
}

# Dummy provider to break circular dependency in EKS module
provider "kubernetes" {
  alias = "dummy"
}

provider "kubernetes" {
  host                   = module.eks.cluster_endpoint
  cluster_ca_certificate = base64decode(module.eks.cluster_certificate_authority_data)
  exec {
    api_version = "client.authentication.k8s.io/v1beta1"
    command     = "aws"
    args        = ["eks", "get-token", "--cluster-name", module.eks.cluster_name]
  }
}

provider "helm" {
  kubernetes {
    host                   = module.eks.cluster_endpoint
    cluster_ca_certificate = base64decode(module.eks.cluster_certificate_authority_data)
    exec {
      api_version = "client.authentication.k8s.io/v1beta1"
      command     = "aws"
      args        = ["eks", "get-token", "--cluster-name", module.eks.cluster_name]
    }
  }
}

# VPC Module
module "vpc" {
  source  = "terraform-aws-modules/vpc/aws"
  version = "5.0.0"

  name = "smartinventory-vpc"
  cidr = var.vpc_cidr

  azs             = var.availability_zones
  private_subnets = var.private_subnets
  public_subnets  = var.public_subnets

  enable_nat_gateway     = true
  single_nat_gateway     = true
  enable_dns_hostnames   = true
  enable_vpn_gateway     = false

  private_subnet_tags = {
    "kubernetes.io/cluster/${var.cluster_name}" = "shared"
    "kubernetes.io/role/internal-elb"             = "1"
  }

  public_subnet_tags = {
    "kubernetes.io/cluster/${var.cluster_name}" = "shared"
    "kubernetes.io/role/elb"                      = "1"
  }

  tags = var.tags
}

# EKS Cluster
module "eks" {
  source  = "terraform-aws-modules/eks/aws"
  version = "19.17.2"

  providers = {
    kubernetes = kubernetes.dummy
  }

  cluster_name    = var.cluster_name
  cluster_version = var.cluster_version

  vpc_id          = module.vpc.vpc_id
  subnet_ids      = module.vpc.private_subnets

  cluster_endpoint_public_access  = true

  manage_aws_auth_configmap = false

  create_kms_key              = false
  create_cloudwatch_log_group = false
  cluster_encryption_config   = {}

  eks_managed_node_groups = {
    main = {
      name           = "main"
      instance_types = ["c7i.large"]

      min_size     = 2
      max_size     = 5
      desired_size = 3

      labels = {
        Environment = "production"
        Application = "smartinventory"
      }
    }
  }

  tags = var.tags
}

# RDS MySQL Database
resource "aws_db_instance" "mysql" {
  identifier     = "smartinventory-mysql"
  engine         = "mysql"
  engine_version = "8.0"
  
  instance_class = var.db_instance_class
  allocated_storage = var.db_storage
  
  db_name  = "smartinventory"
  username = var.db_username
  password = var.db_password
  
  db_subnet_group_name = aws_db_subnet_group.main.name
  vpc_security_group_ids = [aws_security_group.database.id]
  
  backup_retention_period = 0
  backup_window          = "03:00-04:00"
  maintenance_window     = "Mon:04:00-Mon:05:00"
  
  skip_final_snapshot = true
  
  tags = var.tags
}

resource "aws_db_subnet_group" "main" {
  name       = "smartinventory-db-subnet-group-${random_id.bucket_suffix.hex}"
  subnet_ids = module.vpc.private_subnets
  
  tags = var.tags
}

resource "aws_security_group" "database" {
  name        = "smartinventory-db-sg"
  description = "Security group for MySQL database"
  vpc_id      = module.vpc.vpc_id
  
  ingress {
    from_port   = 3306
    to_port     = 3306
    protocol    = "tcp"
    cidr_blocks = [var.vpc_cidr]
  }
  
  egress {
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }
  
  tags = var.tags
}

# S3 for backups
resource "aws_s3_bucket" "backups" {
  bucket = "smartinventory-backups-${random_id.bucket_suffix.hex}"
  
  tags = var.tags
}

resource "aws_s3_bucket_versioning" "backups" {
  bucket = aws_s3_bucket.backups.id
  versioning_configuration {
    status = "Enabled"
  }
}

resource "aws_s3_bucket_lifecycle_configuration" "backups" {
  bucket = aws_s3_bucket.backups.id
  
  rule {
    id     = "backup-lifecycle"
    status = "Enabled"
    
    filter {}
    
    expiration {
      days = 90
    }
    
    noncurrent_version_expiration {
      noncurrent_days = 30
    }
  }
}

resource "random_id" "bucket_suffix" {
  byte_length = 4
}

# Helm Release for SmartInventory
resource "helm_release" "smartinventory" {
  name       = "smartinventory"
  repository = "./helm/smartinventory"
  chart      = "smartinventory"
  namespace  = "default"
  
  set {
    name  = "replicaCount"
    value = "3"
  }
  
  set {
    name  = "image.tag"
    value = var.app_version
  }
  
  set {
    name  = "mysql.enabled"
    value = "false"
  }
  
  set {
    name  = "env.DB_HOST"
    value = aws_db_instance.mysql.endpoint
  }
  
  depends_on = [module.eks]
}
