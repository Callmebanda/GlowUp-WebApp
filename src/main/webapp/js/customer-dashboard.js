document.addEventListener('DOMContentLoaded', function() {
    initializeDashboard();

    // Tab switching
    document.querySelectorAll('.tab-btn').forEach(button => {
        button.addEventListener('click', function() {
            const tab = this.getAttribute('data-tab');
            switchTab(tab);
        });
    });

    // Modal handlers
    document.querySelector('.close-modal').addEventListener('click', closeModal);
    document.getElementById('modal-cancel-btn').addEventListener('click', cancelAppointment);
    document.getElementById('modal-reschedule-btn').addEventListener('click', rescheduleAppointment);
    document.getElementById('logout').addEventListener('click', handleLogout);
});

async function initializeDashboard() {
    try {
        // 1. Get user data
        const userResponse = await fetch('/login', {
            credentials: 'include'
        });

        if (!userResponse.ok) {
            throw new Error('Failed to get user data');
        }

        const userData = await userResponse.json();
        document.getElementById('customer-name').textContent = userData.name;
        console.log("User ID:", userData.userId);

        // 2. Get appointments
        const appointmentsResponse = await fetch(`/appointments?userId=${userData.userId}`, {
            credentials: 'include'
        });

        if (!appointmentsResponse.ok) {
            throw new Error('Failed to fetch appointments');
        }

        const data = await appointmentsResponse.json();
        console.log("Appointments data:", data);

        // 3. Process and display appointments
        const currentDate = new Date();
        const organizedAppointments = {
            upcoming: data.filter(appt =>
                appt.status === 'confirmed' &&
                new Date(appt.appointment_date) >= currentDate
            ),
            pending: data.filter(appt =>
                appt.status === 'pending'
            ),
            history: data.filter(appt =>
                appt.status === 'completed' ||
                appt.status === 'rejected' ||
                new Date(appt.appointment_date) < currentDate
            )
        };

        renderAppointments(organizedAppointments.upcoming, 'upcoming-appointments');
        renderAppointments(organizedAppointments.pending, 'pending-appointments');
        renderAppointments(organizedAppointments.history, 'past-appointments');

    } catch (error) {
        console.error("Dashboard error:", error);
        showErrorMessages();

        // Add debug button
        const debugDiv = document.createElement('div');
        debugDiv.innerHTML = `
            <button id="debug-query" style="margin: 10px; padding: 5px;">
                Debug: Check Database Connection
            </button>
        `;
        document.body.appendChild(debugDiv);
        document.getElementById('debug-query').addEventListener('click', () => {
            alert('Check server logs and database connection for user ID: ' +
                (document.getElementById('customer-name').textContent || 'unknown'));
        });
    }
}

function renderAppointments(appointments, containerId) {
    const container = document.getElementById(containerId);
    container.innerHTML = '';

    if (!appointments || appointments.length === 0) {
        container.innerHTML = '<div class="no-appointments">No appointments found</div>';
        return;
    }

    appointments.forEach(appointment => {
        const appointmentEl = document.createElement('div');
        appointmentEl.className = 'appointment-card';
        appointmentEl.innerHTML = `
            <h3>${appointment.service_name}</h3>
            <p><strong>Salon:</strong> ${appointment.salon_name || 'N/A'}</p>
            <p><strong>Date:</strong> ${formatDate(appointment.appointment_date)}</p>
            <p><strong>Time:</strong> ${formatTime(appointment.start_time)}</p>
            <p><strong>Status:</strong> <span class="status-${appointment.status.toLowerCase()}">${appointment.status}</span></p>
            <button class="btn view-details" data-id="${appointment.appointment_id}">View Details</button>
        `;
        container.appendChild(appointmentEl);
    });

    // Add click handlers for view details buttons
    document.querySelectorAll('.view-details').forEach(button => {
        button.addEventListener('click', function() {
            const appointmentId = this.getAttribute('data-id');
            showAppointmentDetails(appointmentId);
        });
    });
}

function formatDate(dateString) {
    try {
        const options = { year: 'numeric', month: 'long', day: 'numeric' };
        return new Date(dateString).toLocaleDateString('en-US', options);
    } catch (e) {
        console.error("Invalid date format:", dateString);
        return dateString; // Return raw string if date parsing fails
    }
}

function formatTime(timeString) {
    try {
        const timePart = timeString.includes(' ') ?
            timeString.split(' ')[1] : timeString;

        const [hours, minutes] = timePart.split(':');
        const hour = parseInt(hours);
        const ampm = hour >= 12 ? 'PM' : 'AM';
        const hour12 = hour % 12 || 12;
        return `${hour12}:${minutes} ${ampm}`;
    } catch (e) {
        console.error("Invalid time format:", timeString);
        return timeString; // Return raw string if time parsing fails
    }
}

function switchTab(tab) {
    document.querySelectorAll('.tab-content').forEach(content => {
        content.classList.remove('active');
    });
    document.getElementById(`${tab}-tab`).classList.add('active');

    document.querySelectorAll('.tab-btn').forEach(button => {
        button.classList.remove('active');
    });
    document.querySelector(`.tab-btn[data-tab="${tab}"]`).classList.add('active');
}

async function showAppointmentDetails(appointmentId) {
    try {
        const response = await fetch(`/appointments?appointmentId=${appointmentId}`, {
            credentials: 'include'
        });

        if (!response.ok) {
            throw new Error('Failed to fetch appointment details');
        }

        const appointment = await response.json();

        document.getElementById('appointment-salon').textContent = appointment.salon_name || 'N/A';
        document.getElementById('appointment-datetime').textContent =
            `${formatDate(appointment.appointment_date)} at ${formatTime(appointment.start_time)}`;
        document.getElementById('appointment-service').textContent = appointment.service_name;
        document.getElementById('appointment-status').textContent = appointment.status;

        document.getElementById('appointment-modal').setAttribute('data-appointment-id', appointmentId);
        document.getElementById('appointment-modal').classList.add('active');
    } catch (error) {
        console.error('Error loading appointment details:', error);
        alert('Failed to load appointment details');
    }
}

function closeModal() {
    document.getElementById('appointment-modal').classList.remove('active');
}

async function cancelAppointment() {
    const appointmentId = document.getElementById('appointment-modal').getAttribute('data-appointment-id');

    if (!confirm('Are you sure you want to cancel this appointment?')) {
        return;
    }

    try {
        const response = await fetch(`/appointments?appointmentId=${appointmentId}`, {
            method: 'DELETE',
            credentials: 'include'
        });

        if (!response.ok) {
            throw new Error('Failed to cancel appointment');
        }

        const result = await response.json();
        if (result.success) {
            alert('Appointment canceled successfully');
            await initializeDashboard(); // Refresh appointments
            closeModal();
        } else {
            throw new Error(result.error || 'Failed to cancel appointment');
        }
    } catch (error) {
        console.error('Error canceling appointment:', error);
        alert(error.message || 'Failed to cancel appointment');
    }
}

function rescheduleAppointment() {
    const appointmentId = document.getElementById('appointment-modal').getAttribute('data-appointment-id');
    alert(`Redirecting to reschedule appointment ${appointmentId}`);
    closeModal();
}

function handleLogout(e) {
    e.preventDefault();
    fetch('/login', {
        method: 'DELETE',
        credentials: 'include'
    })
        .then(response => {
            if (response.ok) {
                window.location.href = '/login.html';
            } else {
                throw new Error('Logout failed');
            }
        })
        .catch(error => {
            console.error('Logout error:', error);
            window.location.href = '/login.html';
        });
}

function showErrorMessages() {
    document.querySelectorAll('.appointments-list').forEach(list => {
        list.innerHTML = '<div class="error">Failed to load appointments. Please try again later.</div>';
    });
}