
import {
    fetchAppointments,
    organizeAppointments,
    renderAppointments,
    updateAppointmentStatus,
    formatDate,
    formatTime
} from './salonDashboard.js';

// Mock the global fetch
global.fetch = jest.fn();

describe('Salon Dashboard Functions', () => {
    beforeEach(() => {
        fetch.mockClear();
        document.body.innerHTML = `
      <div id="pending-tab">
        <div id="pending-appointments"></div>
      </div>
      <div id="upcoming-tab">
        <div id="upcoming-appointments"></div>
      </div>
      <div id="history-tab">
        <div id="history-appointments"></div>
      </div>
    `;
    });

    // Test fetchAppointments
    describe('fetchAppointments', () => {
        it('should fetch appointments successfully', async () => {
            const mockAppointments = [{ id: 1, status: 'pending' }];
            fetch.mockResolvedValueOnce({
                ok: true,
                json: () => Promise.resolve(mockAppointments)
            });

            const result = await fetchAppointments('pending');
            expect(result).toEqual(mockAppointments);
            expect(fetch).toHaveBeenCalledWith('appointments?type=pending');
        });

        it('should handle fetch error', async () => {
            fetch.mockRejectedValueOnce(new Error('Network error'));
            const result = await fetchAppointments('pending');
            expect(result).toEqual([]);
        });
    });

    // Test organizeAppointments
    describe('organizeAppointments', () => {
        const today = new Date();
        const tomorrow = new Date(today);
        tomorrow.setDate(today.getDate() + 1);
        const yesterday = new Date(today);
        yesterday.setDate(today.getDate() - 1);

        const testAppointments = [
            { appointment_id: 1, status: 'pending', appointment_date: today.toISOString().split('T')[0] },
            { appointment_id: 2, status: 'confirmed', appointment_date: tomorrow.toISOString().split('T')[0] },
            { appointment_id: 3, status: 'confirmed', appointment_date: yesterday.toISOString().split('T')[0] },
            { appointment_id: 4, status: 'cancelled', appointment_date: tomorrow.toISOString().split('T')[0] },
        ];

        it('should organize appointments correctly', () => {
            const result = organizeAppointments(testAppointments);

            expect(result.pending.length).toBe(1);
            expect(result.pending[0].appointment_id).toBe(1);

            expect(result.upcoming.length).toBe(1);
            expect(result.upcoming[0].appointment_id).toBe(2);

            expect(result.history.length).toBe(2);
            expect(result.history.some(a => a.appointment_id === 3)).toBe(true);
            expect(result.history.some(a => a.appointment_id === 4)).toBe(true);
        });
    });

    // Test renderAppointments
    describe('renderAppointments', () => {
        it('should render appointments to the container', () => {
            const appointments = [
                {
                    appointment_id: 1,
                    customer_name: 'John Doe',
                    service_name: 'Haircut',
                    appointment_date: '2023-06-15',
                    start_time: '10:00:00',
                    status: 'pending'
                }
            ];

            renderAppointments(appointments, 'pending-appointments');

            const container = document.getElementById('pending-appointments');
            expect(container.innerHTML).toContain('John Doe');
            expect(container.innerHTML).toContain('Haircut');
            expect(container.querySelector('.accept')).not.toBeNull();
        });

        it('should handle empty container', () => {
            renderAppointments([], 'nonexistent-container');
            // Should not throw error
        });
    });

    // Test updateAppointmentStatus
    describe('updateAppointmentStatus', () => {
        beforeEach(() => {
            fetch.mockClear();
            fetch.mockResolvedValueOnce({
                ok: true,
                json: () => Promise.resolve({ success: true })
            });
        });

        it('should send correct request for accept', async () => {
            await updateAppointmentStatus(123, 'accept');

            expect(fetch).toHaveBeenCalledWith('appointments', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded',
                },
                body: 'appointmentId=123&action=accept'
            });
        });

        it('should handle fetch error', async () => {
            fetch.mockRejectedValueOnce(new Error('Network error'));
            await updateAppointmentStatus(123, 'accept');
            // Should not throw, just log error
        });
    });

    // Test utility functions
    describe('formatDate', () => {
        it('should format date correctly', () => {
            const result = formatDate('2023-06-15');
            expect(result).toMatch(/June 15, 2023/);
        });
    });

    describe('formatTime', () => {
        it('should format AM time correctly', () => {
            const result = formatTime('09:30:00');
            expect(result).toBe('9:30 AM');
        });

        it('should format PM time correctly', () => {
            const result = formatTime('14:30:00');
            expect(result).toBe('2:30 PM');
        });
    });
});