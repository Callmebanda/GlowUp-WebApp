// salon-dashboard.js
document.addEventListener('DOMContentLoaded', function () {
    // Fetch salon appointments from backend
    fetch('/appointments?type=all')
        .then(response => response.json())
        .then(data => {
            // Organize appointments by status
            const organizedAppointments = {
                pending: data.filter(appt => appt.status === 'Pending'),
                upcoming: data.filter(appt => appt.status === 'Confirmed' && new Date(appt.appointmentDate) >= new Date()),
                history: data.filter(appt => appt.status === 'Completed' || appt.status === 'Rejected' || new Date(appt.appointmentDate) < new Date())
            };

            loadAppointments(organizedAppointments);
        })
        .catch(error => {
            console.error('Error loading appointments:', error);
            document.querySelectorAll('.appointments-list').forEach(list => {
                list.innerHTML = '<div class="error">Failed to load appointments. Please try again later.</div>';
            });
        });

    // Handle accept/reject buttons (event delegation)
    document.addEventListener('click', function(e) {
        if (e.target.classList.contains('accept')) {
            const appointmentId = e.target.getAttribute('data-id');
            updateAppointmentStatus(appointmentId, 'Confirmed');
        }

        if (e.target.classList.contains('reject')) {
            const appointmentId = e.target.getAttribute('data-id');
            updateAppointmentStatus(appointmentId, 'Rejected');
        }
    });
});

function updateAppointmentStatus(appointmentId, newStatus) {
    fetch('/appointments', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/x-www-form-urlencoded',
        },
        body: `appointmentId=${appointmentId}&action=${newStatus.toLowerCase()}`
    })
        .then(response => {
            if (!response.ok) {
                throw new Error('Failed to update appointment');
            }
            return response.json();
        })
        .then(data => {
            if (data.success) {
                alert(`Appointment has been ${newStatus.toLowerCase()}`);
                // Refresh the appointments list
                window.location.reload();
            } else {
                showError(data.error || 'Failed to update appointment');
            }
        })
        .catch(error => {
            console.error('Error:', error);
            showError('Failed to update appointment. Please try again.');
        });
}

// Rest of your existing functions...
document.addEventListener('DOMContentLoaded', function() {
    // In a real app, you would fetch pending appointments for this salon from your database
    const pendingAppointments = JSON.parse(localStorage.getItem('appointments')) || [];

    // Filter appointments for this salon (in a real app, you would query by salonId)
    const salonAppointments = pendingAppointments.filter(app => app.status === "Pending");

    renderAppointments(salonAppointments);
});

function renderAppointments(appointments) {
    const container = document.getElementById('pending-appointments');
    container.innerHTML = '';

    if (appointments.length === 0) {
        container.innerHTML = '<div class="no-appointments">No pending appointments</div>';
        return;
    }

    appointments.forEach(appointment => {
        const appointmentEl = document.createElement('div');
        appointmentEl.className = 'appointment-card';
        appointmentEl.innerHTML = `
            <h3>Appointment #${appointment.serviceId}</h3>
            <p><strong>Date:</strong> ${appointment.date}</p>
            <p><strong>Time:</strong> ${appointment.time}</p>
            <p><strong>Notes:</strong> ${appointment.notes || 'None'}</p>
            <div class="appointment-actions">
                <button class="btn accept" data-id="${appointment.serviceId}">Accept</button>
                <button class="btn reject" data-id="${appointment.serviceId}">Reject</button>
            </div>
        `;
        container.appendChild(appointmentEl);
    });

    // Add event listeners for accept/reject buttons
    document.querySelectorAll('.accept').forEach(button => {
        button.addEventListener('click', function() {
            updateAppointmentStatus(this.getAttribute('data-id'), "Confirmed");
        });
    });

    document.querySelectorAll('.reject').forEach(button => {
        button.addEventListener('click', function() {
            updateAppointmentStatus(this.getAttribute('data-id'), "Rejected");
        });
    });
}

async function updateAppointmentStatus(appointmentId, action) {
    try {
        // Use the correct path (remove leading slash if needed)
        const response = await fetch('appointments', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded',
            },
            body: `appointmentId=${appointmentId}&action=${action}`
        });

        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }

        const result = await response.json();

        if (result.success) {
            alert(`Appointment ${action}ed successfully!`);
            // Refresh only the appointments instead of whole page
            renderAppointments(organizedAppointments.pending, 'pending-appointments');
            renderAppointments(organizedAppointments.upcoming, 'upcoming-appointments');
            renderAppointments(organizedAppointments.history, 'past-appointments');
        } else {
            alert(`Failed to ${action} appointment: ${result.error}`);
        }
    } catch (error) {
        console.error('Error:', error);
        alert(`Failed to ${action} appointment. Please try again.`);
    }
}
