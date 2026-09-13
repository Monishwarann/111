/* ==========================================================================
   HRMS NEXUS - SINGLE PAGE APPLICATION JAVASCRIPT ARCHITECTURE
   ========================================================================== */

let currentUser = null;
let employeesCache = [];
let activeLeaveFilter = 'ALL';
let activeReportType = 'employees';

document.addEventListener('DOMContentLoaded', () => {
    initTheme();
    initClock();
    checkAuthSession();
    setupEventListeners();
});

/* ==========================================================================
   AUTHENTICATION & SESSION MANAGEMENT
   ========================================================================== */

async function checkAuthSession() {
    try {
        const response = await fetch('/api/auth/me');
        if (response.ok) {
            currentUser = await response.json();
            onLoginSuccess();
        } else {
            showLoginModal();
        }
    } catch (err) {
        showLoginModal();
    }
}

function showLoginModal() {
    currentUser = null;
    document.getElementById('login-overlay').classList.remove('hidden');
    document.getElementById('app-container').classList.add('hidden');
}

function fillDemoCredentials(username, password) {
    document.getElementById('login-username').value = username;
    document.getElementById('login-password').value = password;
}

document.getElementById('login-form').addEventListener('submit', async (e) => {
    e.preventDefault();
    const btn = document.getElementById('login-btn');
    const origText = btn.innerHTML;
    btn.innerHTML = '<i class="fa-solid fa-circle-notch fa-spin"></i> Authenticating...';
    btn.disabled = true;

    const username = document.getElementById('login-username').value.trim();
    const password = document.getElementById('login-password').value.trim();

    try {
        const response = await fetch('/api/auth/login', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ username, password })
        });

        if (response.ok) {
            currentUser = await response.json();
            showToast('Login successful! Welcome back ' + currentUser.employeeName, 'success');
            onLoginSuccess();
        } else {
            const errData = await response.json();
            showToast(errData.message || 'Invalid credentials', 'error');
        }
    } catch (err) {
        showToast('Server connection failed. Is Spring Boot running?', 'error');
    } finally {
        btn.innerHTML = origText;
        btn.disabled = false;
    }
});

async function logout() {
    try {
        await fetch('/api/auth/logout', { method: 'POST' });
        showToast('Logged out successfully', 'success');
    } catch (err) {
        console.error(err);
    } finally {
        showLoginModal();
    }
}

function onLoginSuccess() {
    document.getElementById('login-overlay').classList.add('hidden');
    document.getElementById('app-container').classList.remove('hidden');

    // Update Header Profile
    document.getElementById('user-name-display').innerText = currentUser.employeeName || currentUser.username;
    document.getElementById('user-role-badge').innerText = currentUser.role;
    
    const initials = (currentUser.employeeName || currentUser.username).split(' ').map(n => n[0]).join('').substring(0, 2).toUpperCase();
    document.getElementById('user-avatar-initials').innerText = initials;

    // Apply role-based UI restrictions
    applyRolePermissions();

    // Cache employees for dropdowns
    loadEmployeeCache();

    // Check attendance status for quick punch clock
    checkTodayAttendance();

    // Router default to dashboard
    navigateTo('dashboard');
}

function applyRolePermissions() {
    const isEmployee = currentUser.role === 'EMPLOYEE';
    
    // Hide add/edit buttons for employee role if necessary
    document.querySelectorAll('.role-restricted-admin-hr').forEach(el => {
        if (isEmployee) {
            el.classList.add('hidden');
        } else {
            el.classList.remove('hidden');
        }
    });

    // Employee role restricted navigation
    if (isEmployee) {
        document.querySelector('.nav-item[data-view="reports"]').classList.add('hidden');
    } else {
        document.querySelector('.nav-item[data-view="reports"]').classList.remove('hidden');
    }
}

/* ==========================================================================
   NAVIGATION & ROUTER
   ========================================================================== */

function setupEventListeners() {
    // Navigation Items
    document.querySelectorAll('.nav-item').forEach(item => {
        item.addEventListener('click', (e) => {
            e.preventDefault();
            const viewName = item.getAttribute('data-view');
            navigateTo(viewName);
        });
    });

    // Mobile Sidebar Toggle
    document.getElementById('mobile-menu-toggle').addEventListener('click', () => {
        document.querySelector('.sidebar').classList.add('open');
    });
    document.getElementById('mobile-sidebar-close').addEventListener('click', () => {
        document.querySelector('.sidebar').classList.remove('open');
    });

    // Logout
    document.getElementById('logout-btn').addEventListener('click', logout);

    // Close Modals
    document.querySelectorAll('.close-modal').forEach(btn => {
        btn.addEventListener('click', () => {
            btn.closest('.modal-backdrop').classList.add('hidden');
        });
    });

    // Employee Search & Status Filter
    document.getElementById('employee-search-input').addEventListener('input', filterEmployeeTable);
    document.getElementById('employee-status-filter').addEventListener('change', loadEmployees);

    // Add Employee Button
    document.getElementById('add-employee-btn').addEventListener('click', () => {
        document.getElementById('employee-form').reset();
        document.getElementById('emp-id').value = '';
        document.getElementById('employee-modal-title').innerText = 'Add New Employee';
        openModal('employee-modal');
    });

    // Employee Form Submit
    document.getElementById('employee-form').addEventListener('submit', handleEmployeeFormSubmit);

    // Attendance Date Filter
    const todayStr = new Date().toISOString().split('T')[0];
    document.getElementById('attendance-date-filter').value = todayStr;
    document.getElementById('attendance-date-filter').addEventListener('change', loadAttendance);

    // Mark Attendance Button
    document.getElementById('mark-attendance-btn').addEventListener('click', () => {
        document.getElementById('attendance-form').reset();
        document.getElementById('att-id').value = '';
        document.getElementById('att-date').value = todayStr;
        document.getElementById('attendance-modal-title').innerText = 'Mark Attendance';
        openModal('attendance-modal');
    });
    document.getElementById('attendance-form').addEventListener('submit', handleAttendanceFormSubmit);

    // Leave Filter Tabs
    document.querySelectorAll('.tab-btn[data-leave-filter]').forEach(tab => {
        tab.addEventListener('click', () => {
            document.querySelectorAll('.tab-btn[data-leave-filter]').forEach(t => t.classList.remove('active'));
            tab.classList.add('active');
            activeLeaveFilter = tab.getAttribute('data-leave-filter');
            loadLeaves();
        });
    });

    // Apply Leave Button
    document.getElementById('apply-leave-btn').addEventListener('click', () => {
        document.getElementById('leave-form').reset();
        document.getElementById('leave-start').value = todayStr;
        document.getElementById('leave-end').value = todayStr;
        updateLeaveDaysPreview();
        openModal('leave-modal');
    });
    document.getElementById('leave-start').addEventListener('change', updateLeaveDaysPreview);
    document.getElementById('leave-end').addEventListener('change', updateLeaveDaysPreview);
    document.getElementById('leave-form').addEventListener('submit', handleLeaveFormSubmit);

    // Theme Toggle & Quick Punch
    const themeBtn = document.getElementById('theme-toggle-btn');
    if (themeBtn) themeBtn.addEventListener('click', toggleTheme);

    const quickPunchBtn = document.getElementById('quick-punch-btn');
    if (quickPunchBtn) quickPunchBtn.addEventListener('click', handleQuickPunch);

    // Export CSV
    const exportCsvBtn = document.getElementById('export-employees-csv');
    if (exportCsvBtn) exportCsvBtn.addEventListener('click', exportEmployeesCSV);

    // Batch Payroll Button
    const batchPayBtn = document.getElementById('batch-payroll-btn');
    if (batchPayBtn) batchPayBtn.addEventListener('click', handleBatchPayroll);

    // Add Payroll Button
    document.getElementById('add-payroll-btn').addEventListener('click', () => {
        document.getElementById('payroll-form').reset();
        document.getElementById('pay-id').value = '';
        document.getElementById('pay-month').value = new Date().getMonth() + 1;
        document.getElementById('pay-year').value = new Date().getFullYear();
        document.getElementById('payroll-modal-title').innerText = 'Generate Payroll';
        updatePayrollNetPreview();
        openModal('payroll-modal');
    });
    document.getElementById('pay-basic').addEventListener('input', updatePayrollNetPreview);
    document.getElementById('pay-allowances').addEventListener('input', updatePayrollNetPreview);
    document.getElementById('pay-deductions').addEventListener('input', updatePayrollNetPreview);
    document.getElementById('pay-employee').addEventListener('change', (e) => {
        const empId = e.target.value;
        const emp = employeesCache.find(x => x.id == empId);
        if (emp && emp.salary) {
            document.getElementById('pay-basic').value = emp.salary;
            updatePayrollNetPreview();
        }
    });
    document.getElementById('payroll-form').addEventListener('submit', handlePayrollFormSubmit);

    // Performance Add Button
    document.getElementById('add-performance-btn').addEventListener('click', () => {
        document.getElementById('performance-form').reset();
        document.getElementById('perf-id').value = '';
        document.getElementById('perf-date').value = todayStr;
        document.getElementById('performance-modal-title').innerText = 'Add Performance Review';
        openModal('performance-modal');
    });
    document.getElementById('performance-form').addEventListener('submit', handlePerformanceFormSubmit);

    // Reports Tabs
    document.querySelectorAll('.report-tab-btn').forEach(btn => {
        btn.addEventListener('click', () => {
            document.querySelectorAll('.report-tab-btn').forEach(b => b.classList.remove('active'));
            btn.classList.add('active');
            activeReportType = btn.getAttribute('data-report-type');
            renderReportFilters();
            loadReports();
        });
    });

    document.getElementById('apply-report-filters').addEventListener('click', loadReports);
    document.getElementById('reset-report-filters').addEventListener('click', () => {
        document.getElementById('report-filter-form').reset();
        loadReports();
    });
    document.getElementById('export-csv-btn').addEventListener('click', exportReportCSV);
    document.getElementById('print-report-btn').addEventListener('click', () => window.print());
}

function navigateTo(viewName) {
    document.querySelectorAll('.nav-item').forEach(item => {
        if (item.getAttribute('data-view') === viewName) {
            item.classList.add('active');
        } else {
            item.classList.remove('active');
        }
    });

    document.querySelectorAll('.view-section').forEach(sec => sec.classList.remove('active'));
    
    const targetSec = document.getElementById('view-' + viewName);
    if (targetSec) {
        targetSec.classList.add('active');
    }

    const titleMap = {
        dashboard: 'Executive Dashboard',
        employees: 'Employee Management Directory',
        attendance: 'Time & Attendance Logs',
        leaves: 'Leave Management Workflow',
        payroll: 'Enterprise Payroll Ledger',
        performance: 'Performance Reviews & Ratings',
        reports: 'Reports & Business Intelligence'
    };

    document.getElementById('view-title').innerText = titleMap[viewName] || 'Dashboard';
    document.querySelector('.sidebar').classList.remove('open');

    // Load dynamic data for target view
    switch(viewName) {
        case 'dashboard': loadDashboard(); break;
        case 'employees': loadEmployees(); break;
        case 'attendance': loadAttendance(); break;
        case 'leaves': loadLeaves(); break;
        case 'payroll': loadPayroll(); break;
        case 'performance': loadPerformance(); break;
        case 'reports': 
            renderReportFilters();
            loadReports(); 
            break;
    }
}

/* ==========================================================================
   CACHE HELPERS
   ========================================================================== */

async function loadEmployeeCache() {
    try {
        const response = await fetch('/api/employees');
        if (response.ok) {
            employeesCache = await response.json();
            populateEmployeeDropdowns();
        }
    } catch (err) {
        console.error('Failed to load employee cache:', err);
    }
}

function populateEmployeeDropdowns() {
    const dropdowns = ['att-employee', 'leave-employee', 'pay-employee', 'perf-employee'];
    dropdowns.forEach(id => {
        const select = document.getElementById(id);
        if (!select) return;
        select.innerHTML = '<option value="">Select Employee...</option>';
        employeesCache.forEach(emp => {
            select.innerHTML += `<option value="${emp.id}">${emp.name} (${emp.employeeCode}) - ${emp.department}</option>`;
        });
    });
}

/* ==========================================================================
   1. DASHBOARD MODULE
   ========================================================================== */

async function loadDashboard() {
    try {
        const response = await fetch('/api/dashboard/summary');
        if (response.ok) {
            const data = await response.json();

            // Populate KPI metrics
            document.getElementById('kpi-total-emp').innerText = data.totalEmployees || 0;
            document.getElementById('kpi-active-emp').innerText = data.activeEmployees || 0;
            document.getElementById('kpi-present-today').innerText = data.presentToday || 0;
            document.getElementById('kpi-absent-today').innerText = data.absentToday || 0;
            document.getElementById('kpi-late-today').innerText = data.lateToday || 0;
            document.getElementById('kpi-pending-leaves').innerText = data.pendingLeaves || 0;
            document.getElementById('kpi-pending-payroll').innerText = data.pendingPayroll || 0;
            document.getElementById('kpi-avg-rating').innerText = (data.averageRating || 0.0).toFixed(1);

            // Department Distribution
            const deptContainer = document.getElementById('dept-distribution-list');
            deptContainer.innerHTML = '';
            if (data.departmentDistribution && Object.keys(data.departmentDistribution).length > 0) {
                const total = data.totalEmployees || 1;
                Object.entries(data.departmentDistribution).forEach(([dept, count]) => {
                    const pct = Math.round((count / total) * 100);
                    deptContainer.innerHTML += `
                        <div class="dept-item">
                            <div class="dept-info">
                                <span>${dept}</span>
                                <span>${count} Staff (${pct}%)</span>
                            </div>
                            <div class="dept-bar-bg">
                                <div class="dept-bar-fill" style="width: ${pct}%;"></div>
                            </div>
                        </div>
                    `;
                });
            } else {
                deptContainer.innerHTML = '<p class="text-muted">No department data available.</p>';
            }

            // Recent Employees
            const empTbl = document.getElementById('recent-employees-tbl');
            empTbl.innerHTML = '';
            if (data.recentEmployees && data.recentEmployees.length > 0) {
                data.recentEmployees.forEach(emp => {
                    empTbl.innerHTML += `
                        <tr>
                            <td><strong>${emp.employeeCode}</strong></td>
                            <td>${emp.name}</td>
                            <td>${emp.department}</td>
                            <td><span class="badge badge-${emp.status}">${emp.status}</span></td>
                        </tr>
                    `;
                });
            } else {
                empTbl.innerHTML = '<tr><td colspan="4" class="text-muted">No recent employees found.</td></tr>';
            }

            // Recent Leaves
            const leaveTbl = document.getElementById('recent-leaves-tbl');
            leaveTbl.innerHTML = '';
            if (data.recentLeaves && data.recentLeaves.length > 0) {
                data.recentLeaves.forEach(l => {
                    leaveTbl.innerHTML += `
                        <tr>
                            <td>${l.employeeName}</td>
                            <td>${l.leaveType}</td>
                            <td>${l.numberOfDays} Days</td>
                            <td><span class="badge badge-${l.status}">${l.status}</span></td>
                        </tr>
                    `;
                });
            } else {
                leaveTbl.innerHTML = '<tr><td colspan="4" class="text-muted">No pending leave requests.</td></tr>';
            }
        }
    } catch (err) {
        showToast('Failed to load dashboard summary metrics', 'error');
    }
}

/* ==========================================================================
   2. EMPLOYEES MODULE
   ========================================================================== */

async function loadEmployees() {
    const status = document.getElementById('employee-status-filter').value;
    let url = '/api/employees';
    if (status) url += `?status=${status}`;

    try {
        const response = await fetch(url);
        if (response.ok) {
            const employees = await response.json();
            renderEmployeeTable(employees);
        }
    } catch (err) {
        showToast('Failed to fetch employees list', 'error');
    }
}

function renderEmployeeTable(employees) {
    const tbody = document.getElementById('employees-table-body');
    tbody.innerHTML = '';

    if (!employees || employees.length === 0) {
        tbody.innerHTML = `<tr><td colspan="10" class="text-center text-muted py-4">No employee records found. Click "Add Employee" to create one.</td></tr>`;
        return;
    }

    employees.forEach(emp => {
        const isEmployeeRole = currentUser.role === 'EMPLOYEE';
        const actionsHtml = `
            <button class="btn-icon text-info" onclick="viewEmployeeDetail(${emp.id})" title="View Profile"><i class="fa-solid fa-eye"></i></button>
            ${isEmployeeRole ? '' : `
                <button class="btn-icon" onclick="editEmployee(${emp.id})" title="Edit Employee"><i class="fa-solid fa-pen"></i></button>
                <button class="btn-icon" onclick="toggleEmployeeStatus(${emp.id}, '${emp.status}')" title="${emp.status === 'ACTIVE' ? 'Deactivate' : 'Activate'}">
                    <i class="fa-solid ${emp.status === 'ACTIVE' ? 'fa-user-minus' : 'fa-user-check'}"></i>
                </button>
                <button class="btn-icon text-danger" onclick="deleteEmployee(${emp.id})" title="Delete Employee"><i class="fa-solid fa-trash"></i></button>
            `}
        `;

        tbody.innerHTML += `
            <tr>
                <td><strong>${emp.employeeCode}</strong></td>
                <td><a href="javascript:void(0)" onclick="viewEmployeeDetail(${emp.id})" class="emp-name-link">${emp.name}</a></td>
                <td>${emp.email}</td>
                <td>${emp.phone || '-'}</td>
                <td>${emp.department}</td>
                <td>${emp.designation}</td>
                <td>${emp.joiningDate}</td>
                <td>$${(emp.salary || 0).toLocaleString('en-US', { minimumFractionDigits: 2 })}</td>
                <td><span class="badge badge-${emp.status}">${emp.status}</span></td>
                <td class="text-right">${actionsHtml}</td>
            </tr>
        `;
    });
}

function filterEmployeeTable() {
    const term = document.getElementById('employee-search-input').value.toLowerCase();
    const rows = document.querySelectorAll('#employees-table-body tr');

    rows.forEach(row => {
        const text = row.innerText.toLowerCase();
        if (text.includes(term)) {
            row.style.display = '';
        } else {
            row.style.display = 'none';
        }
    });
}

async function handleEmployeeFormSubmit(e) {
    e.preventDefault();
    const id = document.getElementById('emp-id').value;
    const dto = {
        employeeCode: document.getElementById('emp-code').value.trim(),
        name: document.getElementById('emp-name').value.trim(),
        email: document.getElementById('emp-email').value.trim(),
        phone: document.getElementById('emp-phone').value.trim(),
        department: document.getElementById('emp-dept').value.trim(),
        designation: document.getElementById('emp-desig').value.trim(),
        joiningDate: document.getElementById('emp-joining').value,
        salary: parseFloat(document.getElementById('emp-salary').value),
        status: document.getElementById('emp-status').value
    };

    const method = id ? 'PUT' : 'POST';
    const url = id ? `/api/employees/${id}` : '/api/employees';

    try {
        const response = await fetch(url, {
            method: method,
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(dto)
        });

        if (response.ok) {
            showToast(`Employee ${id ? 'updated' : 'created'} successfully!`, 'success');
            closeModal('employee-modal');
            loadEmployees();
            loadEmployeeCache();
            loadDashboard();
        } else if (response.status === 404) {
            showToast('Record not found.', 'error');
        } else {
            const err = await response.json().catch(() => ({ message: 'Validation failed' }));
            showToast(err.message || 'Validation failed', 'error');
        }
    } catch (err) {
        showToast('Database connection failed. Please check the server.', 'error');
    }
}

async function editEmployee(id) {
    try {
        const response = await fetch(`/api/employees/${id}`);
        if (response.ok) {
            const emp = await response.json();
            document.getElementById('emp-id').value = emp.id;
            document.getElementById('emp-code').value = emp.employeeCode;
            document.getElementById('emp-name').value = emp.name;
            document.getElementById('emp-email').value = emp.email;
            document.getElementById('emp-phone').value = emp.phone || '';
            document.getElementById('emp-dept').value = emp.department;
            document.getElementById('emp-desig').value = emp.designation;
            document.getElementById('emp-joining').value = emp.joiningDate;
            document.getElementById('emp-salary').value = emp.salary;
            document.getElementById('emp-status').value = emp.status;

            document.getElementById('employee-modal-title').innerText = 'Edit Employee';
            openModal('employee-modal');
        }
    } catch (err) {
        showToast('Failed to fetch employee details', 'error');
    }
}

async function toggleEmployeeStatus(id, currentStatus) {
    const newStatus = currentStatus === 'ACTIVE' ? 'INACTIVE' : 'ACTIVE';
    try {
        const empRes = await fetch(`/api/employees/${id}`);
        if (empRes.ok) {
            const emp = await empRes.json();
            emp.status = newStatus;
            const updateRes = await fetch(`/api/employees/${id}`, {
                method: 'PUT',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(emp)
            });

            if (updateRes.ok) {
                showToast(`Employee marked as ${newStatus}`, 'success');
                loadEmployees();
                loadDashboard();
            } else if (updateRes.status === 404) {
                showToast('Record not found.', 'error');
            }
        }
    } catch (err) {
        showToast('Database connection failed. Please check the server.', 'error');
    }
}

async function deleteEmployee(id) {
    if (!confirm('Are you sure you want to deactivate/delete this employee?')) return;

    try {
        const response = await fetch(`/api/employees/${id}`, { method: 'DELETE' });
        if (response.status === 204 || response.ok) {
            showToast('Employee deactivated successfully', 'success');
            loadEmployees();
            loadEmployeeCache();
            loadDashboard();
        } else if (response.status === 404) {
            showToast('Record not found.', 'error');
        } else {
            showToast('Delete operation failed', 'error');
        }
    } catch (err) {
        showToast('Database connection failed. Please check the server.', 'error');
    }
}

/* ==========================================================================
   3. ATTENDANCE MODULE
   ========================================================================== */

async function loadAttendance() {
    const date = document.getElementById('attendance-date-filter').value;
    let url = '/api/attendance';
    
    if (currentUser.role === 'EMPLOYEE' && currentUser.employeeId) {
        url = `/api/attendance/employee/${currentUser.employeeId}`;
    } else if (date) {
        url = `/api/attendance/date/${date}`;
    }

    try {
        const response = await fetch(url);
        if (response.ok) {
            const attendance = await response.json();
            renderAttendanceTable(attendance);
        }
    } catch (err) {
        showToast('Failed to fetch attendance logs', 'error');
    }
}

function renderAttendanceTable(attendance) {
    const tbody = document.getElementById('attendance-table-body');
    tbody.innerHTML = '';

    if (!attendance || attendance.length === 0) {
        tbody.innerHTML = `<tr><td colspan="9" class="text-center text-muted py-4">No attendance records logged for this filter.</td></tr>`;
        return;
    }

    attendance.forEach(att => {
        const isEmployeeRole = currentUser.role === 'EMPLOYEE';
        const actionsHtml = isEmployeeRole ? '' : `
            <button class="btn-icon text-danger" onclick="deleteAttendance(${att.id})"><i class="fa-solid fa-trash"></i></button>
        `;

        tbody.innerHTML += `
            <tr>
                <td><strong>${att.date}</strong></td>
                <td>${att.employeeCode}</td>
                <td>${att.employeeName}</td>
                <td>${att.department}</td>
                <td>${att.checkIn || '-'}</td>
                <td>${att.checkOut || '-'}</td>
                <td>${att.workingHours != null ? att.workingHours + ' hrs' : '-'}</td>
                <td><span class="badge badge-${att.status}">${att.status}</span></td>
                <td class="text-right ${isEmployeeRole ? 'hidden' : ''}">${actionsHtml}</td>
            </tr>
        `;
    });
}

async function handleAttendanceFormSubmit(e) {
    e.preventDefault();
    const id = document.getElementById('att-id').value;
    const dto = {
        employeeId: parseInt(document.getElementById('att-employee').value),
        date: document.getElementById('att-date').value,
        checkIn: document.getElementById('att-checkin').value || null,
        checkOut: document.getElementById('att-checkout').value || null,
        status: document.getElementById('att-status').value || null
    };

    const method = id ? 'PUT' : 'POST';
    const url = id ? `/api/attendance/${id}` : '/api/attendance';

    try {
        const response = await fetch(url, {
            method: method,
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(dto)
        });

        if (response.ok) {
            showToast('Attendance logged successfully', 'success');
            closeModal('attendance-modal');
            loadAttendance();
            loadDashboard();
        } else if (response.status === 404) {
            showToast('Record not found.', 'error');
        } else {
            const err = await response.json().catch(() => ({ message: 'Attendance validation failed' }));
            showToast(err.message || 'Attendance validation failed', 'error');
        }
    } catch (err) {
        showToast('Database connection failed. Please check the server.', 'error');
    }
}

async function deleteAttendance(id) {
    if (!confirm('Are you sure you want to delete this attendance record?')) return;
    try {
        const res = await fetch(`/api/attendance/${id}`, { method: 'DELETE' });
        if (res.ok || res.status === 204) {
            showToast('Attendance record deleted', 'success');
            loadAttendance();
            loadDashboard();
        } else if (res.status === 404) {
            showToast('Record not found.', 'error');
        }
    } catch (err) {
        showToast('Database connection failed. Please check the server.', 'error');
    }
}

/* ==========================================================================
   4. LEAVE MODULE
   ========================================================================== */

async function loadLeaves() {
    let url = '/api/leaves';
    if (currentUser.role === 'EMPLOYEE' && currentUser.employeeId) {
        url = `/api/leaves/employee/${currentUser.employeeId}`;
    } else if (activeLeaveFilter !== 'ALL') {
        url = `/api/leaves/status/${activeLeaveFilter}`;
    }

    try {
        const response = await fetch(url);
        if (response.ok) {
            const leaves = await response.json();
            renderLeavesTable(leaves);
        }
    } catch (err) {
        showToast('Failed to fetch leave applications', 'error');
    }
}

function renderLeavesTable(leaves) {
    const tbody = document.getElementById('leaves-table-body');
    tbody.innerHTML = '';

    if (!leaves || leaves.length === 0) {
        tbody.innerHTML = `<tr><td colspan="10" class="text-center text-muted py-4">No leave applications found.</td></tr>`;
        return;
    }

    leaves.forEach(l => {
        const canApprove = (currentUser.role === 'ADMIN' || currentUser.role === 'HR') && l.status === 'PENDING';
        const actionsHtml = canApprove ? `
            <button class="btn btn-success btn-sm" onclick="approveLeave(${l.id})"><i class="fa-solid fa-check"></i> Approve</button>
            <button class="btn btn-danger btn-sm" onclick="rejectLeave(${l.id})"><i class="fa-solid fa-xmark"></i> Reject</button>
        ` : `
            <button class="btn-icon text-danger" onclick="deleteLeave(${l.id})" title="Delete"><i class="fa-solid fa-trash"></i></button>
        `;

        const formattedCreated = l.createdAt ? l.createdAt.split('T')[0] : '-';

        tbody.innerHTML += `
            <tr>
                <td>${formattedCreated}</td>
                <td><strong>${l.employeeCode}</strong></td>
                <td>${l.employeeName}</td>
                <td>${l.leaveType}</td>
                <td>${l.startDate}</td>
                <td>${l.endDate}</td>
                <td><strong>${l.numberOfDays} Days</strong></td>
                <td>${l.reason || '-'}</td>
                <td><span class="badge badge-${l.status}">${l.status}</span></td>
                <td class="text-right">${actionsHtml}</td>
            </tr>
        `;
    });
}

function updateLeaveDaysPreview() {
    const startVal = document.getElementById('leave-start').value;
    const endVal = document.getElementById('leave-end').value;
    const preview = document.getElementById('leave-days-preview');

    if (startVal && endVal) {
        const d1 = new Date(startVal);
        const d2 = new Date(endVal);
        if (d2 >= d1) {
            const diffTime = Math.abs(d2 - d1);
            const diffDays = Math.ceil(diffTime / (1000 * 60 * 60 * 24)) + 1;
            preview.innerText = `${diffDays} Day(s)`;
            return;
        }
    }
    preview.innerText = '1 Day(s)';
}

async function handleLeaveFormSubmit(e) {
    e.preventDefault();
    const dto = {
        employeeId: parseInt(document.getElementById('leave-employee').value),
        leaveType: document.getElementById('leave-type').value,
        startDate: document.getElementById('leave-start').value,
        endDate: document.getElementById('leave-end').value,
        reason: document.getElementById('leave-reason').value.trim()
    };

    try {
        const response = await fetch('/api/leaves', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(dto)
        });

        if (response.ok) {
            showToast('Leave request submitted successfully!', 'success');
            closeModal('leave-modal');
            loadLeaves();
            loadDashboard();
        } else if (response.status === 404) {
            showToast('Record not found.', 'error');
        } else {
            const err = await response.json().catch(() => ({ message: 'Overlapping or invalid dates' }));
            showToast(err.message || 'Overlapping or invalid dates', 'error');
        }
    } catch (err) {
        showToast('Database connection failed. Please check the server.', 'error');
    }
}

async function approveLeave(id) {
    try {
        const res = await fetch(`/api/leaves/${id}/approve`, { method: 'PUT' });
        if (res.ok) {
            showToast('Leave approved successfully', 'success');
            loadLeaves();
            loadDashboard();
        } else if (res.status === 404) {
            showToast('Record not found.', 'error');
        } else {
            const err = await res.json().catch(() => ({ message: 'Approval failed' }));
            showToast(err.message || 'Approval action failed', 'error');
        }
    } catch (err) {
        showToast('Database connection failed. Please check the server.', 'error');
    }
}

async function rejectLeave(id) {
    try {
        const res = await fetch(`/api/leaves/${id}/reject`, { method: 'PUT' });
        if (res.ok) {
            showToast('Leave request rejected', 'warning');
            loadLeaves();
            loadDashboard();
        } else if (res.status === 404) {
            showToast('Record not found.', 'error');
        } else {
            const err = await res.json().catch(() => ({ message: 'Rejection failed' }));
            showToast(err.message || 'Rejection action failed', 'error');
        }
    } catch (err) {
        showToast('Database connection failed. Please check the server.', 'error');
    }
}

async function deleteLeave(id) {
    if (!confirm('Are you sure you want to delete this leave record?')) return;
    try {
        const res = await fetch(`/api/leaves/${id}`, { method: 'DELETE' });
        if (res.ok || res.status === 204) {
            showToast('Leave record deleted', 'success');
            loadLeaves();
            loadDashboard();
        } else if (res.status === 404) {
            showToast('Record not found.', 'error');
        }
    } catch (err) {
        showToast('Database connection failed. Please check the server.', 'error');
    }
}

/* ==========================================================================
   5. PAYROLL MODULE
   ========================================================================== */

async function loadPayroll() {
    let url = '/api/payroll';
    if (currentUser.role === 'EMPLOYEE' && currentUser.employeeId) {
        url = `/api/payroll/employee/${currentUser.employeeId}`;
    }

    try {
        const response = await fetch(url);
        if (response.ok) {
            const payrolls = await response.json();
            renderPayrollTable(payrolls);
        }
    } catch (err) {
        showToast('Failed to fetch payroll statement', 'error');
    }
}

function renderPayrollTable(payrolls) {
    const tbody = document.getElementById('payroll-table-body');
    tbody.innerHTML = '';

    if (!payrolls || payrolls.length === 0) {
        tbody.innerHTML = `<tr><td colspan="10" class="text-center text-muted py-4">No payroll records generated yet.</td></tr>`;
        return;
    }

    payrolls.forEach(p => {
        const canManage = currentUser.role === 'ADMIN' || currentUser.role === 'HR';
        const actionsHtml = `
            <button class="btn btn-outline-secondary btn-sm" onclick="viewSalarySlip(${p.id})"><i class="fa-solid fa-receipt"></i> Slip</button>
            ${canManage && p.paymentStatus === 'PENDING' ? `<button class="btn btn-success btn-sm" onclick="markPayrollPaid(${p.id})"><i class="fa-solid fa-dollar-sign"></i> Pay</button>` : ''}
            ${canManage ? `<button class="btn-icon text-danger" onclick="deletePayroll(${p.id})"><i class="fa-solid fa-trash"></i></button>` : ''}
        `;

        tbody.innerHTML += `
            <tr>
                <td><strong>${p.month}/${p.year}</strong></td>
                <td>${p.employeeCode}</td>
                <td>${p.employeeName}</td>
                <td>${p.department}</td>
                <td>$${(p.basicSalary || 0).toFixed(2)}</td>
                <td>+$${(p.allowances || 0).toFixed(2)}</td>
                <td>-$${(p.deductions || 0).toFixed(2)}</td>
                <td><strong class="text-success">$${(p.netSalary || 0).toFixed(2)}</strong></td>
                <td><span class="badge badge-${p.paymentStatus}">${p.paymentStatus}</span></td>
                <td class="text-right">${actionsHtml}</td>
            </tr>
        `;
    });
}

function updatePayrollNetPreview() {
    const basic = parseFloat(document.getElementById('pay-basic').value) || 0;
    const allowances = parseFloat(document.getElementById('pay-allowances').value) || 0;
    const deductions = parseFloat(document.getElementById('pay-deductions').value) || 0;
    const net = basic + allowances - deductions;
    document.getElementById('pay-net-preview').innerText = `$${net.toFixed(2)}`;
}

async function handlePayrollFormSubmit(e) {
    e.preventDefault();
    const id = document.getElementById('pay-id').value;
    const dto = {
        employeeId: parseInt(document.getElementById('pay-employee').value),
        month: parseInt(document.getElementById('pay-month').value),
        year: parseInt(document.getElementById('pay-year').value),
        basicSalary: parseFloat(document.getElementById('pay-basic').value),
        allowances: parseFloat(document.getElementById('pay-allowances').value) || 0.0,
        deductions: parseFloat(document.getElementById('pay-deductions').value) || 0.0,
        paymentStatus: document.getElementById('pay-status').value
    };

    const method = id ? 'PUT' : 'POST';
    const url = id ? `/api/payroll/${id}` : '/api/payroll';

    try {
        const response = await fetch(url, {
            method: method,
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(dto)
        });

        if (response.ok) {
            showToast('Payroll record saved successfully', 'success');
            closeModal('payroll-modal');
            loadPayroll();
            loadDashboard();
        } else if (response.status === 404) {
            showToast('Record not found.', 'error');
        } else {
            const err = await response.json().catch(() => ({ message: 'Payroll validation failed' }));
            showToast(err.message || 'Payroll validation failed', 'error');
        }
    } catch (err) {
        showToast('Database connection failed. Please check the server.', 'error');
    }
}

async function markPayrollPaid(id) {
    try {
        const res = await fetch(`/api/payroll/${id}/pay`, { method: 'PUT' });
        if (res.ok) {
            showToast('Payroll marked as PAID', 'success');
            loadPayroll();
            loadDashboard();
        } else if (res.status === 404) {
            showToast('Record not found.', 'error');
        } else {
            showToast('Failed to update payroll status', 'error');
        }
    } catch (err) {
        showToast('Database connection failed. Please check the server.', 'error');
    }
}

async function viewSalarySlip(id) {
    try {
        const res = await fetch(`/api/payroll/${id}`);
        if (res.ok) {
            const p = await res.json();
            document.getElementById('slip-period').innerText = `Period: Month ${p.month} / ${p.year}`;
            document.getElementById('slip-emp-name').innerText = p.employeeName;
            document.getElementById('slip-emp-code').innerText = p.employeeCode;
            document.getElementById('slip-emp-dept').innerText = p.department;
            document.getElementById('slip-emp-desig').innerText = p.designation;
            document.getElementById('slip-pay-status').innerText = p.paymentStatus;
            document.getElementById('slip-pay-status').className = `badge badge-${p.paymentStatus}`;
            document.getElementById('slip-pay-date').innerText = p.paymentDate || 'Pending';

            document.getElementById('slip-basic').innerText = `$${(p.basicSalary || 0).toFixed(2)}`;
            document.getElementById('slip-allowances').innerText = `$${(p.allowances || 0).toFixed(2)}`;
            document.getElementById('slip-deductions').innerText = `$${(p.deductions || 0).toFixed(2)}`;
            document.getElementById('slip-net').innerText = `$${(p.netSalary || 0).toFixed(2)}`;

            openModal('salary-slip-modal');
        } else if (res.status === 404) {
            showToast('Record not found.', 'error');
        }
    } catch (err) {
        showToast('Database connection failed. Please check the server.', 'error');
    }
}

async function deletePayroll(id) {
    if (!confirm('Are you sure you want to delete this payroll record?')) return;
    try {
        const res = await fetch(`/api/payroll/${id}`, { method: 'DELETE' });
        if (res.ok || res.status === 204) {
            showToast('Payroll record deleted', 'success');
            loadPayroll();
            loadDashboard();
        } else if (res.status === 404) {
            showToast('Record not found.', 'error');
        } else {
            showToast('Failed to delete payroll record', 'error');
        }
    } catch (err) {
        showToast('Database connection failed. Please check the server.', 'error');
    }
}

/* ==========================================================================
   6. PERFORMANCE MODULE
   ========================================================================== */

async function loadPerformance() {
    let url = '/api/performance';
    if (currentUser.role === 'EMPLOYEE' && currentUser.employeeId) {
        url = `/api/performance/employee/${currentUser.employeeId}`;
    }

    try {
        const response = await fetch(url);
        if (response.ok) {
            const reviews = await response.json();
            renderPerformanceTable(reviews);
        }
    } catch (err) {
        showToast('Failed to fetch performance reviews', 'error');
    }
}

function renderPerformanceTable(reviews) {
    const tbody = document.getElementById('performance-table-body');
    tbody.innerHTML = '';

    if (!reviews || reviews.length === 0) {
        tbody.innerHTML = `<tr><td colspan="8" class="text-center text-muted py-4">No performance reviews recorded yet.</td></tr>`;
        return;
    }

    reviews.forEach(r => {
        const isEmployeeRole = currentUser.role === 'EMPLOYEE';
        const actionsHtml = isEmployeeRole ? '' : `
            <button class="btn-icon text-danger" onclick="deletePerformance(${r.id})"><i class="fa-solid fa-trash"></i></button>
        `;

        // Render Star Rating
        let starsHtml = '';
        const rating = r.rating || 0;
        for (let i = 1; i <= 5; i++) {
            if (i <= rating) {
                starsHtml += '<i class="fa-solid fa-star text-warning" style="color:#eab308"></i>';
            } else if (i - 0.5 <= rating) {
                starsHtml += '<i class="fa-solid fa-star-half-stroke text-warning" style="color:#eab308"></i>';
            } else {
                starsHtml += '<i class="fa-regular fa-star text-muted"></i>';
            }
        }

        tbody.innerHTML += `
            <tr>
                <td>${r.reviewDate}</td>
                <td><strong>${r.employeeCode}</strong></td>
                <td>${r.employeeName}</td>
                <td>${starsHtml} (${rating})</td>
                <td><span class="badge badge-role">${r.performanceLevel}</span></td>
                <td>${r.reviewer}</td>
                <td><small>${r.goals ? '<strong>Goals:</strong> ' + r.goals + '<br>' : ''}${r.feedback ? '<strong>Feedback:</strong> ' + r.feedback : '-'}</small></td>
                <td class="text-right ${isEmployeeRole ? 'hidden' : ''}">${actionsHtml}</td>
            </tr>
        `;
    });
}

async function handlePerformanceFormSubmit(e) {
    e.preventDefault();
    const id = document.getElementById('perf-id').value;
    const dto = {
        employeeId: parseInt(document.getElementById('perf-employee').value),
        reviewDate: document.getElementById('perf-date').value,
        rating: parseFloat(document.getElementById('perf-rating').value),
        reviewer: document.getElementById('perf-reviewer').value.trim(),
        goals: document.getElementById('perf-goals').value.trim(),
        feedback: document.getElementById('perf-feedback').value.trim()
    };

    const method = id ? 'PUT' : 'POST';
    const url = id ? `/api/performance/${id}` : '/api/performance';

    try {
        const response = await fetch(url, {
            method: method,
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(dto)
        });

        if (response.ok) {
            showToast('Performance review saved!', 'success');
            closeModal('performance-modal');
            loadPerformance();
            loadDashboard();
        } else if (response.status === 404) {
            showToast('Record not found.', 'error');
        } else {
            const err = await response.json().catch(() => ({ message: 'Rating must be between 1 and 5' }));
            showToast(err.message || 'Rating must be between 1 and 5', 'error');
        }
    } catch (err) {
        showToast('Database connection failed. Please check the server.', 'error');
    }
}

async function deletePerformance(id) {
    if (!confirm('Are you sure you want to delete this performance review?')) return;
    try {
        const res = await fetch(`/api/performance/${id}`, { method: 'DELETE' });
        if (res.ok || res.status === 204) {
            showToast('Review deleted', 'success');
            loadPerformance();
            loadDashboard();
        } else if (res.status === 404) {
            showToast('Record not found.', 'error');
        } else {
            showToast('Failed to delete performance review', 'error');
        }
    } catch (err) {
        showToast('Database connection failed. Please check the server.', 'error');
    }
}

/* ==========================================================================
   7. REPORTS & ANALYTICS MODULE
   ========================================================================== */

function renderReportFilters() {
    const form = document.getElementById('report-filter-form');
    form.innerHTML = '';

    if (activeReportType === 'employees') {
        form.innerHTML = `
            <div class="form-group">
                <label>Department</label>
                <input type="text" id="rf-dept" placeholder="Filter by department...">
            </div>
            <div class="form-group">
                <label>Designation</label>
                <input type="text" id="rf-desig" placeholder="Filter by designation...">
            </div>
            <div class="form-group">
                <label>Status</label>
                <select id="rf-status" class="form-control">
                    <option value="">All Statuses</option>
                    <option value="ACTIVE">ACTIVE</option>
                    <option value="INACTIVE">INACTIVE</option>
                </select>
            </div>
            <div class="form-group">
                <label>Joining Date From</label>
                <input type="date" id="rf-date-from">
            </div>
            <div class="form-group">
                <label>Joining Date To</label>
                <input type="date" id="rf-date-to">
            </div>
        `;
    } else if (activeReportType === 'attendance') {
        form.innerHTML = `
            <div class="form-group">
                <label>Department</label>
                <input type="text" id="rf-dept" placeholder="Filter department...">
            </div>
            <div class="form-group">
                <label>Status</label>
                <select id="rf-status" class="form-control">
                    <option value="">All Statuses</option>
                    <option value="PRESENT">PRESENT</option>
                    <option value="ABSENT">ABSENT</option>
                    <option value="HALF_DAY">HALF_DAY</option>
                    <option value="LATE">LATE</option>
                </select>
            </div>
            <div class="form-group">
                <label>Date From</label>
                <input type="date" id="rf-date-from">
            </div>
            <div class="form-group">
                <label>Date To</label>
                <input type="date" id="rf-date-to">
            </div>
        `;
    } else if (activeReportType === 'leaves') {
        form.innerHTML = `
            <div class="form-group">
                <label>Leave Type</label>
                <select id="rf-leave-type" class="form-control">
                    <option value="">All Types</option>
                    <option value="CASUAL">CASUAL</option>
                    <option value="SICK">SICK</option>
                    <option value="EARNED">EARNED</option>
                    <option value="UNPAID">UNPAID</option>
                </select>
            </div>
            <div class="form-group">
                <label>Status</label>
                <select id="rf-status" class="form-control">
                    <option value="">All Statuses</option>
                    <option value="PENDING">PENDING</option>
                    <option value="APPROVED">APPROVED</option>
                    <option value="REJECTED">REJECTED</option>
                </select>
            </div>
            <div class="form-group">
                <label>Date From</label>
                <input type="date" id="rf-date-from">
            </div>
            <div class="form-group">
                <label>Date To</label>
                <input type="date" id="rf-date-to">
            </div>
        `;
    } else if (activeReportType === 'payroll') {
        form.innerHTML = `
            <div class="form-group">
                <label>Month (1-12)</label>
                <input type="number" id="rf-month" min="1" max="12" placeholder="e.g. 9">
            </div>
            <div class="form-group">
                <label>Year</label>
                <input type="number" id="rf-year" min="2000" placeholder="e.g. 2026">
            </div>
            <div class="form-group">
                <label>Payment Status</label>
                <select id="rf-status" class="form-control">
                    <option value="">All Statuses</option>
                    <option value="PENDING">PENDING</option>
                    <option value="PAID">PAID</option>
                </select>
            </div>
        `;
    } else if (activeReportType === 'performance') {
        form.innerHTML = `
            <div class="form-group">
                <label>Min Rating</label>
                <input type="number" step="0.1" id="rf-rating" min="1" max="5" placeholder="e.g. 4.0">
            </div>
            <div class="form-group">
                <label>Reviewer</label>
                <input type="text" id="rf-reviewer" placeholder="Filter reviewer...">
            </div>
            <div class="form-group">
                <label>Review Date From</label>
                <input type="date" id="rf-date-from">
            </div>
            <div class="form-group">
                <label>Review Date To</label>
                <input type="date" id="rf-date-to">
            </div>
        `;
    }
}

async function loadReports() {
    let url = `/api/reports/${activeReportType}?`;

    const dept = document.getElementById('rf-dept') ? document.getElementById('rf-dept').value : '';
    const desig = document.getElementById('rf-desig') ? document.getElementById('rf-desig').value : '';
    const status = document.getElementById('rf-status') ? document.getElementById('rf-status').value : '';
    const dateFrom = document.getElementById('rf-date-from') ? document.getElementById('rf-date-from').value : '';
    const dateTo = document.getElementById('rf-date-to') ? document.getElementById('rf-date-to').value : '';
    const leaveType = document.getElementById('rf-leave-type') ? document.getElementById('rf-leave-type').value : '';
    const month = document.getElementById('rf-month') ? document.getElementById('rf-month').value : '';
    const year = document.getElementById('rf-year') ? document.getElementById('rf-year').value : '';
    const rating = document.getElementById('rf-rating') ? document.getElementById('rf-rating').value : '';
    const reviewer = document.getElementById('rf-reviewer') ? document.getElementById('rf-reviewer').value : '';

    if (dept) url += `department=${encodeURIComponent(dept)}&`;
    if (desig) url += `designation=${encodeURIComponent(desig)}&`;
    if (status) url += `status=${encodeURIComponent(status)}&paymentStatus=${encodeURIComponent(status)}&`;
    if (dateFrom) url += `dateFrom=${encodeURIComponent(dateFrom)}&`;
    if (dateTo) url += `dateTo=${encodeURIComponent(dateTo)}&`;
    if (leaveType) url += `leaveType=${encodeURIComponent(leaveType)}&`;
    if (month) url += `month=${encodeURIComponent(month)}&`;
    if (year) url += `year=${encodeURIComponent(year)}&`;
    if (rating) url += `rating=${encodeURIComponent(rating)}&`;
    if (reviewer) url += `reviewer=${encodeURIComponent(reviewer)}&`;

    document.getElementById('report-date-subtitle').innerText = `Generated on ${new Date().toLocaleString()}`;

    try {
        const response = await fetch(url);
        if (response.ok) {
            const data = await response.json();
            renderReportTable(data);
        }
    } catch (err) {
        showToast('Failed to load report data', 'error');
    }
}

function renderReportTable(data) {
    const thead = document.getElementById('report-table-head');
    const tbody = document.getElementById('report-table-body');
    thead.innerHTML = '';
    tbody.innerHTML = '';

    if (!data || data.length === 0) {
        tbody.innerHTML = `<tr><td colspan="8" class="text-center text-muted py-4">No report records found matching filter criteria.</td></tr>`;
        return;
    }

    if (activeReportType === 'employees') {
        thead.innerHTML = `<tr><th>Code</th><th>Name</th><th>Email</th><th>Phone</th><th>Department</th><th>Designation</th><th>Joining Date</th><th>Status</th></tr>`;
        data.forEach(r => {
            tbody.innerHTML += `<tr><td>${r.employeeCode}</td><td>${r.name}</td><td>${r.email}</td><td>${r.phone}</td><td>${r.department}</td><td>${r.designation}</td><td>${r.joiningDate}</td><td><span class="badge badge-${r.status}">${r.status}</span></td></tr>`;
        });
    } else if (activeReportType === 'attendance') {
        thead.innerHTML = `<tr><th>Date</th><th>Code</th><th>Name</th><th>Department</th><th>Check In</th><th>Check Out</th><th>Hours</th><th>Status</th></tr>`;
        data.forEach(r => {
            tbody.innerHTML += `<tr><td>${r.date}</td><td>${r.employeeCode}</td><td>${r.employeeName}</td><td>${r.department}</td><td>${r.checkIn || '-'}</td><td>${r.checkOut || '-'}</td><td>${r.workingHours || 0}</td><td><span class="badge badge-${r.status}">${r.status}</span></td></tr>`;
        });
    } else if (activeReportType === 'leaves') {
        thead.innerHTML = `<tr><th>Submitted</th><th>Code</th><th>Name</th><th>Type</th><th>Start Date</th><th>End Date</th><th>Days</th><th>Status</th></tr>`;
        data.forEach(r => {
            tbody.innerHTML += `<tr><td>${(r.createdAt || '').split('T')[0]}</td><td>${r.employeeCode}</td><td>${r.employeeName}</td><td>${r.leaveType}</td><td>${r.startDate}</td><td>${r.endDate}</td><td>${r.numberOfDays}</td><td><span class="badge badge-${r.status}">${r.status}</span></td></tr>`;
        });
    } else if (activeReportType === 'payroll') {
        thead.innerHTML = `<tr><th>Period</th><th>Code</th><th>Name</th><th>Department</th><th>Basic</th><th>Allowances</th><th>Deductions</th><th>Net Salary</th><th>Status</th></tr>`;
        data.forEach(r => {
            tbody.innerHTML += `<tr><td>${r.month}/${r.year}</td><td>${r.employeeCode}</td><td>${r.employeeName}</td><td>${r.department}</td><td>$${r.basicSalary}</td><td>+$${r.allowances}</td><td>-$${r.deductions}</td><td>$${r.netSalary}</td><td><span class="badge badge-${r.paymentStatus}">${r.paymentStatus}</span></td></tr>`;
        });
    } else if (activeReportType === 'performance') {
        thead.innerHTML = `<tr><th>Date</th><th>Code</th><th>Name</th><th>Rating</th><th>Level</th><th>Reviewer</th><th>Goals</th></tr>`;
        data.forEach(r => {
            tbody.innerHTML += `<tr><td>${r.reviewDate}</td><td>${r.employeeCode}</td><td>${r.employeeName}</td><td>${r.rating} / 5.0</td><td><span class="badge badge-role">${r.performanceLevel}</span></td><td>${r.reviewer}</td><td>${r.goals || '-'}</td></tr>`;
        });
    }
}

function exportReportCSV() {
    const table = document.getElementById('report-data-table');
    let csv = [];
    const rows = table.querySelectorAll('tr');

    rows.forEach(row => {
        const cols = row.querySelectorAll('th, td');
        let rowData = [];
        cols.forEach(col => {
            let text = col.innerText.replace(/"/g, '""').trim();
            rowData.push(`"${text}"`);
        });
        csv.push(rowData.join(','));
    });

    const csvContent = 'data:text/csv;charset=utf-8,' + csv.join('\n');
    const encodedUri = encodeURI(csvContent);
    const link = document.createElement('a');
    link.setAttribute('href', encodedUri);
    link.setAttribute('download', `HRMS_Report_${activeReportType}_${new Date().toISOString().split('T')[0]}.csv`);
    document.body.appendChild(link);
    link.click();
    document.body.removeChild(link);
}

/* ==========================================================================
   UI UTILITIES: TOASTS & MODALS & CLOCK
   ========================================================================== */

function showToast(message, type = 'success') {
    const container = document.getElementById('toast-container');
    const toast = document.createElement('div');
    toast.className = `toast toast-${type}`;

    const iconMap = {
        success: 'fa-circle-check',
        error: 'fa-circle-exclamation',
        warning: 'fa-triangle-exclamation'
    };

    toast.innerHTML = `<i class="fa-solid ${iconMap[type] || 'fa-info-circle'}"></i> <span>${message}</span>`;
    container.appendChild(toast);

    setTimeout(() => {
        toast.remove();
    }, 4000);
}

function openModal(modalId) {
    document.getElementById(modalId).classList.remove('hidden');
}

function closeModal(modalId) {
    document.getElementById(modalId).classList.add('hidden');
}

function initClock() {
    function update() {
        const now = new Date();
        document.getElementById('clock-text').innerText = now.toLocaleTimeString();
    }
    update();
    setInterval(update, 1000);
}

/* ==========================================================================
   FEATURE ENHANCEMENTS: THEME, QUICK PUNCH, EXPORT & PROFILES
   ========================================================================== */

function initTheme() {
    const saved = localStorage.getItem('hrms_theme');
    const toggleBtn = document.getElementById('theme-toggle-btn');
    if (saved === 'light') {
        document.body.classList.remove('dark-theme');
        document.body.classList.add('light-theme');
        if (toggleBtn) toggleBtn.innerHTML = '<i class="fa-solid fa-sun"></i>';
    } else {
        document.body.classList.remove('light-theme');
        document.body.classList.add('dark-theme');
        if (toggleBtn) toggleBtn.innerHTML = '<i class="fa-solid fa-moon"></i>';
    }
}

function toggleTheme() {
    const isLight = document.body.classList.contains('light-theme');
    const toggleBtn = document.getElementById('theme-toggle-btn');
    if (isLight) {
        document.body.classList.remove('light-theme');
        document.body.classList.add('dark-theme');
        localStorage.setItem('hrms_theme', 'dark');
        if (toggleBtn) toggleBtn.innerHTML = '<i class="fa-solid fa-moon"></i>';
        showToast('Switched to Dark Mode', 'info');
    } else {
        document.body.classList.remove('dark-theme');
        document.body.classList.add('light-theme');
        localStorage.setItem('hrms_theme', 'light');
        if (toggleBtn) toggleBtn.innerHTML = '<i class="fa-solid fa-sun"></i>';
        showToast('Switched to Light Mode', 'info');
    }
}

let todayAttendanceRecord = null;

async function checkTodayAttendance() {
    if (!currentUser || !currentUser.employeeId) return;
    const todayStr = new Date().toISOString().split('T')[0];
    try {
        const res = await fetch(`/api/attendance/employee/${currentUser.employeeId}`);
        if (res.ok) {
            const list = await res.json();
            todayAttendanceRecord = list.find(a => a.date === todayStr);
            updatePunchButtonUI();
        }
    } catch(err) {
        console.error(err);
    }
}

function updatePunchButtonUI() {
    const btn = document.getElementById('quick-punch-btn');
    const txt = document.getElementById('punch-btn-text');
    if (!btn || !txt) return;

    if (!todayAttendanceRecord) {
        txt.innerText = 'Punch In';
        btn.className = 'btn btn-emerald btn-sm';
        btn.disabled = false;
    } else if (todayAttendanceRecord.checkIn && !todayAttendanceRecord.checkOut) {
        txt.innerText = 'Punch Out';
        btn.className = 'btn btn-amber btn-sm';
        btn.disabled = false;
    } else {
        txt.innerText = 'Punched Out';
        btn.className = 'btn btn-secondary btn-sm';
        btn.disabled = true;
    }
}

async function handleQuickPunch() {
    if (!currentUser || !currentUser.employeeId) {
        showToast('No linked employee profile for current user account', 'warning');
        return;
    }
    const todayStr = new Date().toISOString().split('T')[0];
    const nowTimeStr = new Date().toTimeString().split(' ')[0].substring(0, 5);

    if (!todayAttendanceRecord) {
        // Mark Punch In
        try {
            const res = await fetch('/api/attendance', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({
                    employeeId: currentUser.employeeId,
                    date: todayStr,
                    checkIn: nowTimeStr,
                    status: 'PRESENT'
                })
            });
            if (res.ok) {
                showToast(`Punched In successfully at ${nowTimeStr}`, 'success');
                checkTodayAttendance();
                if (document.getElementById('view-attendance').classList.contains('active')) loadAttendance();
                if (document.getElementById('view-dashboard').classList.contains('active')) loadDashboard();
            } else {
                const err = await res.json().catch(() => ({ message: 'Punch in failed' }));
                showToast(err.message || 'Failed to punch in', 'error');
            }
        } catch(e) {
            showToast('Punch in failed', 'error');
        }
    } else if (todayAttendanceRecord.checkIn && !todayAttendanceRecord.checkOut) {
        // Mark Punch Out
        try {
            const res = await fetch(`/api/attendance/${todayAttendanceRecord.id}`, {
                method: 'PUT',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({
                    employeeId: currentUser.employeeId,
                    date: todayStr,
                    checkIn: todayAttendanceRecord.checkIn,
                    checkOut: nowTimeStr
                })
            });
            if (res.ok) {
                showToast(`Punched Out successfully at ${nowTimeStr}`, 'success');
                checkTodayAttendance();
                if (document.getElementById('view-attendance').classList.contains('active')) loadAttendance();
                if (document.getElementById('view-dashboard').classList.contains('active')) loadDashboard();
            } else {
                const err = await res.json().catch(() => ({ message: 'Punch out failed' }));
                showToast(err.message || 'Failed to punch out', 'error');
            }
        } catch(e) {
            showToast('Punch out failed', 'error');
        }
    }
}

function exportEmployeesCSV() {
    if (!employeesCache || employeesCache.length === 0) {
        showToast('No employee data available to export', 'warning');
        return;
    }
    const headers = ['ID', 'Code', 'Name', 'Email', 'Phone', 'Department', 'Designation', 'Joining Date', 'Salary', 'Status'];
    const rows = employeesCache.map(e => [
        e.id,
        `"${e.employeeCode}"`,
        `"${e.name}"`,
        `"${e.email}"`,
        `"${e.phone || ''}"`,
        `"${e.department}"`,
        `"${e.designation}"`,
        e.joiningDate,
        e.salary || 0,
        e.status
    ]);

    const csvContent = 'data:text/csv;charset=utf-8,' + [headers.join(','), ...rows.map(r => r.join(','))].join('\n');
    const encodedUri = encodeURI(csvContent);
    const link = document.createElement('a');
    link.setAttribute('href', encodedUri);
    link.setAttribute('download', `HRMS_Employees_Directory_${new Date().toISOString().split('T')[0]}.csv`);
    document.body.appendChild(link);
    link.click();
    document.body.removeChild(link);
    showToast('Employee directory exported to CSV!', 'success');
}

function viewEmployeeDetail(empId) {
    const emp = employeesCache.find(x => x.id == empId);
    if (!emp) return;

    const initials = emp.name.split(' ').map(n => n[0]).join('').substring(0, 2).toUpperCase();
    document.getElementById('detail-emp-avatar').innerText = initials;
    document.getElementById('detail-emp-name').innerText = emp.name;
    document.getElementById('detail-emp-designation').innerText = emp.designation;
    document.getElementById('detail-emp-dept').innerText = emp.department;
    document.getElementById('detail-emp-status').innerText = emp.status;
    document.getElementById('detail-emp-status').className = `badge badge-${emp.status}`;
    document.getElementById('detail-emp-code').innerText = emp.employeeCode;
    document.getElementById('detail-emp-email').innerText = emp.email;
    document.getElementById('detail-emp-phone').innerText = emp.phone || 'N/A';
    document.getElementById('detail-emp-doj').innerText = emp.joiningDate || 'N/A';
    document.getElementById('detail-emp-salary').innerText = `$${(emp.salary || 0).toLocaleString('en-US', { minimumFractionDigits: 2 })}`;
    document.getElementById('detail-emp-emergency').innerText = `${emp.name} Emergency Contact (+1 555-0199)`;

    openModal('employee-detail-modal');
}

async function handleBatchPayroll() {
    if (!confirm('Generate current month payroll records for all active employees?')) return;
    const now = new Date();
    const month = now.getMonth() + 1;
    const year = now.getFullYear();

    try {
        const response = await fetch(`/api/payroll/batch?month=${month}&year=${year}`, {
            method: 'POST'
        });
        if (response.ok) {
            showToast('Batch payroll generated successfully!', 'success');
            loadPayroll();
            if (document.getElementById('view-dashboard').classList.contains('active')) loadDashboard();
        } else {
            showToast('Failed to generate batch payroll', 'error');
        }
    } catch(e) {
        showToast('Batch payroll request failed', 'error');
    }
}
