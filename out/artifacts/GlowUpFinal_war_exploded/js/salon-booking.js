// salon-booking.js
document.addEventListener('DOMContentLoaded', function() {
    // Get salon ID from URL
    const urlParams = new URLSearchParams(window.location.search);
    const salonId = urlParams.get('salonId');

    if (!salonId) {
        showError("No salon selected. Please go back and select a salon.");
        return;
    }

    // In a real app, you would fetch this from your database
    const salonData = {
        id: salonId,
        name: "Glamour Salon",
        address: "123 Beauty Street, Glam City",
        phone: "(555) 123-4567",
        imageUrl: "images/salon1.jpg",
        services: [
            { id: 1, name: "Haircut", price: 45, duration: 30 },
            { id: 2, name: "Hair Color", price: 85, duration: 60 },
            { id: 3, name: "Manicure", price: 25, duration: 30 }
        ]
    };

    // Display salon info
    displaySalonInfo(salonData);

    // Setup form submission
    document.getElementById('bookingForm').addEventListener('submit', function(e) {
        e.preventDefault();
        bookAppointment(salonId);
    });
});

function displaySalonInfo(salon) {
    const salonHeader = document.getElementById('salonHeader');
    salonHeader.innerHTML = `
        <div class="salon-image" style="background-image: url('${salon.imageUrl}')"></div>
        <div class="salon-info">
            <h1>${salon.name}</h1>
            <div class="salon-meta">
                <p>${salon.address}</p>
                <p>${salon.phone}</p>
            </div>
        </div>
    `;

    // Populate services dropdown
    const serviceSelect = document.getElementById('service');
    salon.services.forEach(service => {
        const option = document.createElement('option');
        option.value = service.id;
        option.textContent = `${service.name} - $${service.price} (${service.duration} mins)`;
        serviceSelect.appendChild(option);
    });
}

function bookAppointment(salonId) {
    const serviceId = document.getElementById('service').value;
    const date = document.getElementById('date').value;
    const time = document.getElementById('time').value;
    const notes = document.getElementById('notes').value;

    if (!serviceId || !date || !time) {
        showError("Please fill in all required fields");
        return;
    }

    // In a real app, you would send this data to your server
    const appointment = {
        salonId,
        serviceId,
        date,
        time,
        notes,
        status: "Pending"
    };

    // Save to localStorage (in a real app, this would be your database)
    let appointments = JSON.parse(localStorage.getItem('appointments')) || [];
    appointments.push(appointment);
    localStorage.setItem('appointments', JSON.stringify(appointments));

    // Show success message
    alert("Appointment booked successfully! It's now pending approval from the salon.");

    // Redirect to dashboard
    window.location.href = "customer-dashboard.html";
}

function showError(message) {
    const errorElement = document.getElementById('error-message');
    errorElement.textContent = message;
    errorElement.style.display = 'block';

    // Hide error after 5 seconds
    setTimeout(() => {
        errorElement.style.display = 'none';
    }, 5000);
}