document.addEventListener('DOMContentLoaded', function() {
    const urlParams = new URLSearchParams(window.location.search);
    const salonId = urlParams.get('salonId');

    if (!salonId) {
        window.location.href = '/';
        return;
    }

    loadSalonProfile(salonId);
    setupBookingForm(salonId);
});

async function loadSalonProfile(salonId) {
    try {
        // Load salon details
        const salonResponse = await fetch(`/api/salons/${salonId}`);
        const salon = await salonResponse.json();

        document.getElementById('salon-name').textContent = salon.name;
        document.getElementById('salon-address').textContent = salon.address;
        document.getElementById('salon-phone').textContent = salon.phone;
        document.getElementById('salon-description').textContent = salon.description;

        // Load services
        const servicesResponse = await fetch(`/api/salons/${salonId}/services`);
        const services = await servicesResponse.json();

        const serviceSelect = document.getElementById('service');
        serviceSelect.innerHTML = '<option value="">Select a service</option>';

        services.forEach(service => {
            const option = document.createElement('option');
            option.value = service.serviceId;
            option.textContent = `${service.name} - $${service.price.toFixed(2)} (${service.durationMinutes} mins)`;
            serviceSelect.appendChild(option);
        });

    } catch (error) {
        console.error('Error loading salon profile:', error);
        showError('Failed to load salon information. Please try again later.');
    }
}

function setupBookingForm(salonId) {
    const bookingForm = document.getElementById('booking-form');
    const dateInput = document.getElementById('date');
    const timeInput = document.getElementById('time');
    const serviceSelect = document.getElementById('service');

    // Set minimum date to today
    const today = new Date().toISOString().split('T')[0];
    dateInput.setAttribute('min', today);

    // When service is selected, load available times
    serviceSelect.addEventListener('change', async function() {
        if (!this.value || !dateInput.value) return;
        await loadAvailableTimes(salonId, this.value, dateInput.value);
    });

    // When date changes, load available times if service is selected
    dateInput.addEventListener('change', async function() {
        if (!serviceSelect.value || !this.value) return;
        await loadAvailableTimes(salonId, serviceSelect.value, this.value);
    });

    // Form submission
    bookingForm.addEventListener('submit', async function(e) {
        e.preventDefault();

        if (!validateBookingForm()) return;

        try {
            const formData = {
                salonId: salonId,
                serviceId: serviceSelect.value,
                date: dateInput.value,
                time: timeInput.value,
                notes: document.getElementById('notes').value
            };

            const response = await fetch('/appointments', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(formData)
            });

            if (response.ok) {
                alert('Appointment booked successfully!');
                window.location.href = '/customer-dashboard.html';
            } else {
                throw new Error('Booking failed');
            }
        } catch (error) {
            console.error('Error booking appointment:', error);
            showError('Failed to book appointment. Please try again.');
        }
    });
}

async function loadAvailableTimes(salonId, serviceId, date) {
    try {
        timeInput.disabled = true;
        timeInput.innerHTML = '<option value="">Loading available times...</option>';

        const response = await fetch(`/api/salons/${salonId}/availability?serviceId=${serviceId}&date=${date}`);
        const times = await response.json();

        timeInput.innerHTML = '<option value="">Select a time</option>';
        times.forEach(time => {
            const option = document.createElement('option');
            option.value = time;
            option.textContent = time;
            timeInput.appendChild(option);
        });

        timeInput.disabled = false;

    } catch (error) {
        console.error('Error loading available times:', error);
        timeInput.innerHTML = '<option value="">Failed to load times</option>';
    }
}

function validateBookingForm() {
    const service = document.getElementById('service').value;
    const date = document.getElementById('date').value;
    const time = document.getElementById('time').value;

    if (!service) {
        showError('Please select a service');
        return false;
    }
    if (!date) {
        showError('Please select a date');
        return false;
    }
    if (!time) {
        showError('Please select a time');
        return false;
    }
    return true;
}

function showError(message) {
    const errorEl = document.createElement('div');
    errorEl.className = 'error-message';
    errorEl.textContent = message;

    const form = document.getElementById('booking-form');
    form.prepend(errorEl);

    setTimeout(() => errorEl.remove(), 5000);
}