let bookingData = {
    salonId: 1,
    serviceId: 1,
    userId: 1,
    staffId: null,
    date: null,
    startTime: null,
    endTime: null,
    notes: ''
};

document.addEventListener('DOMContentLoaded', function () {
    const urlParams = new URLSearchParams(window.location.search);
    const salonId = urlParams.get('salonId') || 1;
    const serviceId = urlParams.get('serviceId') || 1;

    bookingData.salonId = parseInt(salonId);
    bookingData.serviceId = parseInt(serviceId);

    setupStepNavigation();
    loadServiceDetails(serviceId);
    setupDatePicker();
    setupBookingForm();
    document.getElementById('confirm-booking').addEventListener('click', confirmBooking);
});

function setupStepNavigation() {
    const nextBtns = document.querySelectorAll('.next-btn');
    const prevBtns = document.querySelectorAll('.prev-btn');

    nextBtns.forEach(btn => {
        btn.addEventListener('click', function () {
            const currentStep = this.closest('.step');
            const nextStep = currentStep.nextElementSibling;
            if (nextStep) {
                currentStep.classList.remove('active');
                nextStep.classList.add('active');
            }
        });
    });

    prevBtns.forEach(btn => {
        btn.addEventListener('click', function () {
            const currentStep = this.closest('.step');
            const prevStep = currentStep.previousElementSibling;
            if (prevStep) {
                currentStep.classList.remove('active');
                prevStep.classList.add('active');
            }
        });
    });
}

function loadServiceDetails(serviceId) {
    fetch(`/services/${serviceId}`)
        .then(response => response.json())
        .then(service => {
            document.getElementById('service-name').textContent = service.name;
            document.getElementById('service-description').textContent = service.description;
            document.getElementById('service-duration').textContent = service.duration + " minutes";

            loadStylists(bookingData.salonId, serviceId);
        })
        .catch(error => console.error('Error loading service details:', error));
}

function loadStylists(salonId, serviceId) {
    fetch(`/stylists?salonId=${salonId}&serviceId=${serviceId}`)
        .then(response => response.json())
        .then(stylists => {
            const stylistList = document.getElementById('stylist-list');
            stylistList.innerHTML = '';

            stylists.forEach(stylist => {
                const div = document.createElement('div');
                div.className = 'stylist-card';
                div.innerHTML = `
                    <img src="${stylist.photoUrl}" alt="${stylist.name}">
                    <h3>${stylist.name}</h3>
                    <button class="select-stylist-btn" data-id="${stylist.id}">Select</button>
                `;
                stylistList.appendChild(div);
            });

            document.querySelectorAll('.select-stylist-btn').forEach(button => {
                button.addEventListener('click', function () {
                    bookingData.staffId = parseInt(this.dataset.id);
                    document.querySelectorAll('.select-stylist-btn').forEach(btn => btn.classList.remove('selected'));
                    this.classList.add('selected');
                    updateBookingSummary();
                });
            });
        })
        .catch(error => console.error('Error loading stylists:', error));
}

function setupDatePicker() {
    flatpickr("#appointment-date", {
        minDate: "today",
        dateFormat: "Y-m-d",
        onChange: function (selectedDates, dateStr) {
            bookingData.date = dateStr;
            loadTimeSlots(dateStr);
        }
    });
}

function loadTimeSlots(date) {
    if (!bookingData.staffId || !bookingData.serviceId) return;

    fetch(`/timeslots?staffId=${bookingData.staffId}&serviceId=${bookingData.serviceId}&date=${date}`)
        .then(response => response.json())
        .then(slots => {
            const slotContainer = document.getElementById('time-slots');
            slotContainer.innerHTML = '';

            slots.forEach(slot => {
                const btn = document.createElement('button');
                btn.className = 'time-slot-btn';
                btn.textContent = formatTime(slot.startTime) + ' - ' + formatTime(slot.endTime);
                btn.dataset.start = slot.startTime;
                btn.dataset.end = slot.endTime;

                btn.addEventListener('click', function () {
                    bookingData.startTime = this.dataset.start;
                    bookingData.endTime = this.dataset.end;

                    document.querySelectorAll('.time-slot-btn').forEach(btn => btn.classList.remove('selected'));
                    this.classList.add('selected');

                    updateBookingSummary();
                });

                slotContainer.appendChild(btn);
            });
        })
        .catch(error => console.error('Error loading time slots:', error));
}

function formatTime(timeStr) {
    const [hour, minute] = timeStr.split(':').map(Number);
    const date = new Date();
    date.setHours(hour, minute);
    return date.toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' });
}

function setupBookingForm() {
    document.getElementById('notes').addEventListener('input', function () {
        bookingData.notes = this.value;
    });
}

function updateBookingSummary() {
    const stylist = document.querySelector('.select-stylist-btn.selected');
    const stylistName = stylist ? stylist.closest('.stylist-card').querySelector('h3').textContent : 'Not selected';
    const serviceName = document.getElementById('service-name').textContent;
    const date = bookingData.date || 'Not selected';
    const time = bookingData.startTime && bookingData.endTime ?
        formatTime(bookingData.startTime) + ' - ' + formatTime(bookingData.endTime) : 'Not selected';

    document.getElementById('summary-service').textContent = serviceName;
    document.getElementById('summary-stylist').textContent = stylistName;
    document.getElementById('summary-datetime').textContent = `${date}, ${time}`;
}

function confirmBooking() {
    fetch('/bookings', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(bookingData)
    })
        .then(response => response.json())
        .then(result => {
            if (result.success) {
                document.getElementById('confirmation-message').textContent = `Booking confirmed! Your appointment ID is ${result.bookingId}.`;
                document.getElementById('confirmation-modal').style.display = 'block';
            } else {
                alert('Booking failed. Please try again.');
            }
        })
        .catch(error => console.error('Error confirming booking:', error));
}
