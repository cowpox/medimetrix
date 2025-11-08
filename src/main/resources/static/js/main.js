function showToast(message, type) {
  const t = document.createElement('div');
  t.className = `toast align-items-center text-bg-${type || 'primary'} border-0 position-fixed top-0 end-0 m-3`;
  t.setAttribute('role', 'alert'); t.setAttribute('aria-live', 'assertive'); t.setAttribute('aria-atomic', 'true');
  t.innerHTML = `<div class="d-flex"><div class="toast-body">${message}</div><button type="button" class="btn-close btn-close-white me-2 m-auto" data-bs-dismiss="toast" aria-label="Close"></button></div>`;
  document.body.appendChild(t);
  const toast = new bootstrap.Toast(t, { delay: 2500 }); toast.show();
  t.addEventListener('hidden.bs.toast', () => t.remove());
}