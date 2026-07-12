import os
import re

target_dir = r"c:\Users\Jakkali Lokesh\Desktop\maven\src\main\webapp\WEB-INF\views"

# List of files to refactor
files_to_refactor = [
    ("categories/list.jsp", "Categories - SmartInventory", "categories", "Categories"),
    ("categories/form.jsp", "${empty category ? 'Add' : 'Edit'} Category - SmartInventory", "categories", "${empty category ? 'Add' : 'Edit'} Category"),
    ("suppliers/list.jsp", "Suppliers - SmartInventory", "suppliers", "Suppliers"),
    ("suppliers/form.jsp", "${empty supplier ? 'Add' : 'Edit'} Supplier - SmartInventory", "suppliers", "${empty supplier ? 'Add' : 'Edit'} Supplier"),
    ("inventory/list.jsp", "Inventory - SmartInventory", "inventory", "Inventory Management"),
    ("inventory/history.jsp", "Transaction History - SmartInventory", "inventory", "Transaction History"),
    ("inventory/stock-in.jsp", "Stock In - SmartInventory", "inventory", "Stock In"),
    ("inventory/stock-out.jsp", "Stock Out - SmartInventory", "inventory", "Stock Out"),
    ("inventory/adjust.jsp", "Stock Adjustment - SmartInventory", "inventory", "Stock Adjustment"),
    ("profile/view.jsp", "My Profile - SmartInventory", "profile", "My Profile"),
    ("profile/edit.jsp", "Edit Profile - SmartInventory", "profile", "Edit Profile"),
    ("profile/change-password.jsp", "Change Password - SmartInventory", "profile", "Change Password"),
    ("users/list.jsp", "Users - SmartInventory", "users", "User Management"),
    ("users/form.jsp", "${empty userObj ? 'Add' : 'Edit'} User - SmartInventory", "users", "${empty userObj ? 'Add' : 'Edit'} User")
]

for relative_path, title, active_tab, page_title in files_to_refactor:
    filepath = os.path.join(target_dir, relative_path.replace('/', os.sep))
    if not os.path.exists(filepath):
        print(f"File not found: {filepath}")
        continue
        
    print(f"Refactoring: {relative_path}")
    
    with open(filepath, 'r', encoding='utf-8') as f:
        content = f.read()
        
    # Check if already refactored
    if "fragments/header.jsp" in content:
        print(f"Already refactored: {relative_path}")
        continue
        
    # Find the taglibs at the top
    taglibs = []
    for line in content.splitlines():
        if line.startswith("<%@"):
            taglibs.append(line)
        elif line.strip() and not line.startswith("<%@"):
            break
            
    # Remove the taglibs from the head to avoid duplicates
    # The header fragment already includes core, we can keep fmt/functions if needed.
    # Let's keep JSTL imports that are needed.
    taglibs_str = "\n".join(taglibs)
    
    # Locate the beginning of <main class="main-content">
    main_start_idx = content.find('<main class="main-content">')
    if main_start_idx == -1:
        print(f"Error: <main class=\"main-content\"> not found in {relative_path}")
        continue
        
    # Locate the end of top-nav
    top_nav_start = content.find('<div class="top-nav">', main_start_idx)
    top_nav_end = -1
    buttons_html = ""
    
    if top_nav_start != -1:
        # Find the matching closing </div> for <div class="top-nav">
        # Let's extract topbar-right actions if any
        # Specifically look for <div class="top-nav-right">...</div>
        right_start = content.find('<div class="top-nav-right">', top_nav_start)
        if right_start != -1:
            # find matching </div> for top-nav-right
            depth = 1
            idx = right_start + len('<div class="top-nav-right">')
            while depth > 0 and idx < len(content):
                if content[idx:idx+4] == '<div':
                    depth += 1
                    idx += 4
                elif content[idx:idx+6] == '</div':
                    depth -= 1
                    idx += 6
                else:
                    idx += 1
            right_end = idx
            buttons_html = content[right_start + len('<div class="top-nav-right">'):right_end - 6].strip()
            
        # Find the end of topbar div
        depth = 1
        idx = top_nav_start + len('<div class="top-nav">')
        while depth > 0 and idx < len(content):
            if content[idx:idx+4] == '<div':
                depth += 1
                idx += 4
            elif content[idx:idx+6] == '</div':
                depth -= 1
                idx += 6
            else:
                idx += 1
        top_nav_end = idx
        
    # Get the inner content inside <main class="main-content"> after </div (top-nav closing)>
    main_inner_start = top_nav_end if top_nav_end != -1 else main_start_idx + len('<main class="main-content">')
    
    # Locate the footer scripts at the end of the file
    # We want to keep everything up to </main>
    main_end_idx = content.rfind('</main>')
    if main_end_idx == -1:
        print(f"Error: </main> not found in {relative_path}")
        continue
        
    main_body = content[main_inner_start:main_end_idx].strip()
    
    # Reconstruct the file
    new_content = ""
    # Add imports
    new_content += taglibs_str + "\n"
    
    # Add header include
    new_content += f'<jsp:include page="/WEB-INF/views/fragments/header.jsp">\n'
    new_content += f'    <jsp:param name="title" value="{title}" />\n'
    new_content += f'</jsp:include>\n\n'
    
    # Add sidebar include
    new_content += f'<jsp:include page="/WEB-INF/views/fragments/sidebar.jsp">\n'
    new_content += f'    <jsp:param name="activeTab" value="{active_tab}" />\n'
    new_content += f'</jsp:include>\n\n'
    
    new_content += '<main class="main-content">\n'
    
    # Add topbar include
    new_content += f'    <jsp:include page="/WEB-INF/views/fragments/topbar.jsp">\n'
    new_content += f'        <jsp:param name="pageTitle" value="{page_title}" />\n'
    new_content += f'    </jsp:include>\n\n'
    
    # If we have action buttons, add them in an actions bar
    if buttons_html:
        new_content += '    <div style="display: flex; justify-content: flex-end; gap: 0.5rem; margin-bottom: 1.25rem;">\n'
        # indent buttons
        indented_buttons = "\n".join([f"        {line.strip()}" for line in buttons_html.splitlines() if line.strip()])
        new_content += indented_buttons + "\n"
        new_content += '    </div>\n\n'
        
    new_content += "    " + main_body + "\n"
    new_content += '</main>\n\n'
    new_content += '<jsp:include page="/WEB-INF/views/fragments/footer.jsp" />\n'
    
    with open(filepath, 'w', encoding='utf-8') as f:
        f.write(new_content)
        
    print(f"Successfully refactored {relative_path}!")
