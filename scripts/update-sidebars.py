#!/usr/bin/env python3
import os

# Automation script to insert Reports sidebar link in all JSP files
target_dir = r"c:\Users\Jakkali Lokesh\Desktop\maven\src\main\webapp\WEB-INF\views"

old_block_1 = '<li><a href="${pageContext.request.contextPath}/inventory/"><i class="fas fa-warehouse"></i> Inventory</a></li>'
new_block_1 = '<li><a href="${pageContext.request.contextPath}/inventory/"><i class="fas fa-warehouse"></i> Inventory</a></li>\n                <li><a href="${pageContext.request.contextPath}/reports/dashboard"><i class="fas fa-chart-bar"></i> Reports</a></li>'

old_block_2 = '<li><a href="${pageContext.request.contextPath}/inventory/" class="active"><i class="fas fa-warehouse"></i> Inventory</a></li>'
new_block_2 = '<li><a href="${pageContext.request.contextPath}/inventory/" class="active"><i class="fas fa-warehouse"></i> Inventory</a></li>\n                <li><a href="${pageContext.request.contextPath}/reports/dashboard"><i class="fas fa-chart-bar"></i> Reports</a></li>'

count = 0

for root, dirs, files in os.walk(target_dir):
    for name in files:
        if name.endswith(".jsp"):
            filepath = os.path.join(root, name)
            with open(filepath, 'r', encoding='utf-8') as f:
                content = f.read()
            
            # Skip if reports is already in sidebar
            if "/reports/dashboard" in content:
                continue
                
            updated = False
            if old_block_1 in content:
                content = content.replace(old_block_1, new_block_1)
                updated = True
            elif old_block_2 in content:
                content = content.replace(old_block_2, new_block_2)
                updated = True
                
            if updated:
                with open(filepath, 'w', encoding='utf-8') as f:
                    f.write(content)
                print(f"Updated sidebar in: {name}")
                count += 1

print(f"Update completed. Total files updated: {count}")
