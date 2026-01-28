document.addEventListener('DOMContentLoaded', function() {
  // Переключение видимости пароля
  document.querySelectorAll('.password-toggle').forEach(button => {
      button.addEventListener('click', function() {
          const targetId = this.getAttribute('data-target');
          const input = document.getElementById(targetId);
          const icon = this.querySelector('i');

          if (input.type === 'password') {
              input.type = 'text';
              icon.classList.remove('fa-eye');
              icon.classList.add('fa-eye-slash');
          } else {
              input.type = 'password';
              icon.classList.remove('fa-eye-slash');
              icon.classList.add('fa-eye');
          }
      });
  });

  // Маска для телефона
  const phoneInput = document.getElementById('registerPhone');
  if (phoneInput) {
      phoneInput.addEventListener('input', function(e) {
          let x = e.target.value.replace(/\D/g, '').match(/(\d{0,1})(\d{0,3})(\d{0,3})(\d{0,2})(\d{0,2})/);
          e.target.value = !x[2] ? '+7' : '+7 (' + x[2] + (x[3] ? ') ' + x[3] : '') + (x[4] ? '-' + x[4] : '') + (x[5] ? '-' + x[5] : '');
      });
  }

  // Проверка паролей при регистрации
  const registerForm = document.getElementById('registerForm');
  if (registerForm) {
      registerForm.addEventListener('submit', function(e) {
          const password = document.getElementById('registerPassword').value;
          const confirmPassword = document.getElementById('registerConfirmPassword').value;
          const message = document.getElementById('authMessage');

          if (password !== confirmPassword) {
              e.preventDefault();
              message.className = 'auth-message error';
              message.textContent = 'Пароли не совпадают!';
              message.style.display = 'block';
              return false;
          }

          if (password.length < 6) {
              e.preventDefault();
              message.className = 'auth-message error';
              message.textContent = 'Пароль должен быть не менее 6 символов!';
              message.style.display = 'block';
              return false;
          }

          // Показываем индикатор загрузки
          const btn = document.getElementById('registerBtn');
          const originalText = btn.innerHTML;
          btn.innerHTML = '<div class="loader"></div> Загрузка...';
          btn.disabled = true;

          // Восстанавливаем кнопку через 5 секунд на случай ошибки
          setTimeout(() => {
              btn.innerHTML = originalText;
              btn.disabled = false;
          }, 5000);
      });
  }

  // Индикатор загрузки для входа
  const loginForm = document.getElementById('loginForm');
  if (loginForm) {
      loginForm.addEventListener('submit', function() {
          const btn = document.getElementById('loginBtn');
          const originalText = btn.innerHTML;
          btn.innerHTML = '<div class="loader"></div> Вход...';
          btn.disabled = true;

          setTimeout(() => {
              btn.innerHTML = originalText;
              btn.disabled = false;
          }, 5000);
      });
  }

  // Социальные кнопки
  document.querySelectorAll('.btn-social').forEach(btn => {
      btn.addEventListener('click', function() {
          const platform = this.querySelector('span').textContent;
          alert(`Вход через ${platform} будет доступен в ближайшее время`);
      });
  });

  // Ссылка "Забыли пароль"
  document.getElementById('forgotPassword')?.addEventListener('click', function(e) {
      e.preventDefault();
      const message = document.getElementById('authMessage');
      message.className = 'auth-message info';
      message.textContent = 'Функция восстановления пароля будет доступна в ближайшее время';
      message.style.display = 'block';
  });

  // Автоматический фокус на первое поле формы
  const activeForm = document.querySelector('.auth-form.active');
  if (activeForm) {
      const firstInput = activeForm.querySelector('input');
      if (firstInput) {
          firstInput.focus();
      }
  }
});

registerForm.addEventListener('submit', function(e) {
    const phoneInput = document.getElementById('registerPhone');
    phoneInput.value = phoneInput.value.replace(/\D/g, '');
});