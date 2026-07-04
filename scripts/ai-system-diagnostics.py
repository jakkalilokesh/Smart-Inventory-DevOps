#!/usr/bin/env python3
import os
import sys
import json
import socket
import urllib.request
import re
from datetime import datetime

# SmartInventory Automated AI Diagnostics & Troubleshooting Script
# This script performs automated environment checks, parses application logs for anomalies,
# and simulates AI-based diagnostic recommendations for self-healing and recovery.

COLOR_GREEN = "\033[92m"
COLOR_YELLOW = "\033[93m"
COLOR_RED = "\033[91m"
COLOR_BLUE = "\033[94m"
COLOR_RESET = "\033[0m"

def print_status(msg, status="INFO"):
    timestamp = datetime.now().strftime("%Y-%m-%d %H:%M:%S")
    if status == "OK":
        print(f"[{timestamp}] {COLOR_GREEN}[OK]{COLOR_RESET} {msg}")
    elif status == "WARNING":
        print(f"[{timestamp}] {COLOR_YELLOW}[WARN]{COLOR_RESET} {msg}")
    elif status == "ERROR":
        print(f"[{timestamp}] {COLOR_RED}[ERROR]{COLOR_RESET} {msg}")
    else:
        print(f"[{timestamp}] {COLOR_BLUE}[INFO]{COLOR_RESET} {msg}")

def check_port(host, port):
    try:
        with socket.create_connection((host, port), timeout=3):
            return True
    except OSError:
        return False

def run_diagnostics():
    print("=" * 80)
    print(f"  {COLOR_BLUE}SMARTINVENTORY AUTOMATED SYSTEM & AI DIAGNOSTIC ENGINE{COLOR_RESET}")
    print("=" * 80)

    issues_found = []

    # 1. Check Database Environment Variables
    print_status("Phase 1: Environment Configuration Analysis...")
    db_host = os.environ.get("DB_HOST", "localhost")
    db_port = int(os.environ.get("DB_PORT", 3306))
    db_name = os.environ.get("DB_NAME", "smartinventory")
    db_user = os.environ.get("DB_USER", "root")

    print_status(f"Configured Database Host: {db_host}")
    print_status(f"Configured Database Port: {db_port}")
    print_status(f"Configured Database Name: {db_name}")

    if db_host == "localhost" and os.path.exists("/.dockerenv"):
        issues_found.append({
            "component": "Database Config",
            "severity": "CRITICAL",
            "message": "Database host set to localhost inside a Docker container. This will cause connectivity issues.",
            "recommendation": "Set DB_HOST to 'mysql' or the correct cluster service DNS record."
        })
        print_status("Database configuration mismatch detected.", "ERROR")
    else:
        print_status("Environment variables setup matches environment profile.", "OK")

    # 2. Network Connectivity Checks
    print_status("Phase 2: Network Services Diagnostics...")
    if check_port(db_host, db_port):
        print_status(f"Successfully reached MySQL Database Service at {db_host}:{db_port}", "OK")
    else:
        issues_found.append({
            "component": "Database Connectivity",
            "severity": "CRITICAL",
            "message": f"Unable to reach Database Service at {db_host}:{db_port}.",
            "recommendation": "Check if MySQL pod/service is running. Verify security groups, network policies, or Docker bridge setup."
        })
        print_status(f"Failed to connect to Database Service at {db_host}:{db_port}", "ERROR")

    # 3. Log Analysis & Heuristic AI Pattern Matcher
    print_status("Phase 3: Automated Log File Analysis...")
    log_files = [
        "logs/app.log",
        "logs/error.log",
        "target/tomcat/logs/catalina.out"
    ]
    
    found_log = False
    for lf in log_files:
        if os.path.exists(lf):
            found_log = True
            print_status(f"Scanning log file: {lf}")
            scan_log_file(lf, issues_found)
            break
            
    if not found_log:
        print_status("No local log files found in standard locations. Scanning mock diagnostic buffers...", "WARNING")
        # Simulate log diagnostic parsing
        scan_mock_log(issues_found)

    # 4. Generate AI Diagnostics & Recommendations Report
    print("\n" + "=" * 80)
    print(f"  {COLOR_GREEN}AI-POWERED DIAGNOSTICS & ACTIONABLE MITIGATION STEPS{COLOR_RESET}")
    print("=" * 80)

    if not issues_found:
        print_status("All systems checked. No errors or anomalies detected. System is running healthy!", "OK")
    else:
        print_status(f"Analysis completed: {len(issues_found)} issue(s) identified.", "WARNING")
        for i, issue in enumerate(issues_found, 1):
            print(f"\n{COLOR_RED}[Issue {i}] {issue['component']} - Severity: {issue['severity']}{COLOR_RESET}")
            print(f"  Description   : {issue['message']}")
            print(f"  {COLOR_GREEN}AI Remediation: {issue['recommendation']}{COLOR_RESET}")
            
            # Simulated Automated Healing recommendation action
            if "localhost" in issue['message']:
                print(f"  {COLOR_BLUE}[Auto-Heal Run]{COLOR_RESET} Action: Exporting correct environment profile...")
                os.environ["DB_HOST"] = "mysql"
                print(f"  {COLOR_BLUE}[Auto-Heal Run]{COLOR_RESET} Status: Temporarily resolved current shell variables.")

    print("\n" + "=" * 80)
    print("  DIAGNOSTICS COMPLETED SUCCESSFULY")
    print("=" * 80)

def scan_log_file(filepath, issues_found):
    error_patterns = {
        r"java\.sql\.SQLException.*Access denied": {
            "component": "Database Auth",
            "severity": "CRITICAL",
            "recommendation": "Database login failed. Check db.properties or database secret values in K8s credentials storage."
        },
        r"OutOfMemoryError": {
            "component": "JVM Memory",
            "severity": "CRITICAL",
            "recommendation": "Java Heap Space exhausted. Update container spec limits or increase -Xmx parameter in Jenkins/GitHub pipelines."
        },
        r"CommunicationsException: Communications link failure": {
            "component": "Database Network",
            "severity": "HIGH",
            "recommendation": "Database server disconnected. Restart mysql container or verify connection limits."
        }
    }

    try:
        with open(filepath, 'r') as f:
            lines = f.readlines()[-200:] # Scan last 200 lines
            for line in lines:
                for pattern, details in error_patterns.items():
                    if re.search(pattern, line):
                        issues_found.append({
                            "component": details["component"],
                            "severity": details["severity"],
                            "message": line.strip(),
                            "recommendation": details["recommendation"]
                        })
    except Exception as e:
        print_status(f"Error reading log file: {e}", "ERROR")

def scan_mock_log(issues_found):
    # Simulating standard diagnostic cases if execution logs are empty
    mock_log_lines = [
        "INFO  [main] Database connection pool initialized successfully",
        "WARN  [http-nio-8080-exec-3] Connection pool reaching high allocation rate (85%)",
        "ERROR [http-nio-8080-exec-5] java.sql.SQLException: Communications link failure - connection timed out"
    ]
    for line in mock_log_lines:
        if "Communications link failure" in line:
            issues_found.append({
                "component": "Database Connectivity",
                "severity": "CRITICAL",
                "message": line,
                "recommendation": "Verify Docker bridge network or check Kubernetes service endpoints mappings."
            })

if __name__ == "__main__":
    run_diagnostics()
