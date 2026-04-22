/**
 * Utility function to convert a date to a string.
 */

export function toDateInputValue(date: Date | string): string {
    if (!date) return '';
    const d = date instanceof Date ? date : new Date(date);
    if (Number.isNaN(d.getTime())) return '';
    return d.toISOString().split('T')[0];
}