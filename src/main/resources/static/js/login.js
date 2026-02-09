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

    // Улучшенная маска для телефона
    const phoneInput = document.getElementById('registerPhone');
    if (phoneInput) {
        // Сохраняем текущее значение из Thymeleaf, если есть
        const currentValue = phoneInput.value;
        if (!currentValue || currentValue.trim() === '') {
            phoneInput.value = '+7 ';
        }

        phoneInput.addEventListener('input', function(e) {
            // Получаем позицию курсора до изменений
            const cursorPos = this.selectionStart;
            const valueBefore = this.value;

            // Получаем только цифры из текущего значения
            let numbers = this.value.replace(/\D/g, '');

            // Если номер начинается не с 7, корректируем
            if (numbers.length > 0 && numbers.charAt(0) !== '7') {
                numbers = '7' + numbers.substring(1);
            }

            // Ограничиваем до 11 цифр
            numbers = numbers.substring(0, 11);

            // Форматируем номер
            let formatted = '';

            if (numbers.length === 0) {
                formatted = '+7 ';
            } else if (numbers.length === 1) {
                formatted = '+7 ';
            } else if (numbers.length <= 4) {
                formatted = '+7 (' + numbers.substring(1);
            } else if (numbers.length <= 7) {
                formatted = '+7 (' + numbers.substring(1, 4) + ') ' + numbers.substring(4);
            } else if (numbers.length <= 9) {
                formatted = '+7 (' + numbers.substring(1, 4) + ') ' + numbers.substring(4, 7) + '-' + numbers.substring(7);
            } else {
                formatted = '+7 (' + numbers.substring(1, 4) + ') ' + numbers.substring(4, 7) + '-' + numbers.substring(7, 9) + '-' + numbers.substring(9);
            }

            // Устанавливаем отформатированное значение
            this.value = formatted;

            // Корректируем позицию курсора
            let newCursorPos = cursorPos;

            // Если добавлялись символы форматирования, сдвигаем курсор
            if (formatted.length > valueBefore.length) {
                const addedChars = formatted.length - valueBefore.length;
                newCursorPos += addedChars;
            } else if (formatted.length < valueBefore.length) {
                const removedChars = valueBefore.length - formatted.length;
                newCursorPos = Math.max(0, newCursorPos - removedChars);
            }

            // Убедимся, что курсор не попадает на символы форматирования
            while (newCursorPos < formatted.length && !/\d/.test(formatted.charAt(newCursorPos)) && formatted.charAt(newCursorPos) !== ' ') {
                newCursorPos++;
            }

            // Устанавливаем курсор
            this.setSelectionRange(newCursorPos, newCursorPos);
        });

        // Обработка клавиш Backspace и Delete
        phoneInput.addEventListener('keydown', function(e) {
            if (e.key === 'Backspace') {
                // Получаем текущую позицию курсора
                const cursorPos = this.selectionStart;
                const value = this.value;

                // Если курсор находится на символе форматирования, перемещаем его назад
                if (cursorPos > 0 && !/\d/.test(value.charAt(cursorPos - 1))) {
                    this.setSelectionRange(cursorPos - 1, cursorPos - 1);
                    e.preventDefault();
                }
            }
        });

        // При фокусе перемещаем курсор в нужное место
        phoneInput.addEventListener('focus', function() {
            const value = this.value;
            let cursorPos = value.length;

            // Ищем позицию после последней цифры
            for (let i = value.length - 1; i >= 0; i--) {
                if (/\d/.test(value.charAt(i))) {
                    cursorPos = i + 1;
                    break;
                }
            }

            // Если нет цифр, ставим курсор после "+7 "
            if (!/\d/.test(value)) {
                cursorPos = 3;
            }

            this.setSelectionRange(cursorPos, cursorPos);
        });

        // Валидация при потере фокуса
        phoneInput.addEventListener('blur', function() {
            const numbers = this.value.replace(/\D/g, '');
            if (numbers.length < 11) {
                // Можно добавить визуальную индикацию
                this.classList.add('border-warning');
            } else {
                this.classList.remove('border-warning');
            }
        });
    }

    // Проверка паролей при регистрации
    const registerForm = document.getElementById('registerForm');
    if (registerForm) {
        registerForm.addEventListener('submit', function(e) {
            const phoneInput = document.getElementById('registerPhone');
                if (phoneInput) {
                    phoneInput.value = phoneInput.value.replace(/\D/g, '');
                    // Проверяем, что номер начинается с 7
                    if (!phoneInput.value.startsWith('7')) {
                      phoneInput.value = '7' + phoneInput.value;
                    }
                }
            const password = document.getElementById('registerPassword').value;
            const confirmPassword = document.getElementById('registerConfirmPassword').value;
            const message = document.getElementById('authMessage');

            // Проверка телефона
            if (phoneInput && phoneInput.value.replace(/\D/g, '').length !== 11) {
                e.preventDefault();
                message.className = 'auth-message error';
                message.textContent = 'Введите полный номер телефона (11 цифр)';
                message.style.display = 'block';

                // Восстанавливаем форматирование
                phoneInput.value = formatPhone(phoneInput.value);
                return false;
            }

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

// Вспомогательная функция для форматирования телефона
function formatPhone(numbers) {
    if (!numbers) return '+7 ';

    numbers = numbers.replace(/\D/g, '');
    if (numbers.length > 0 && numbers[0] !== '7') {
        numbers = '7' + numbers.substring(1);
    }

    let formatted = '+7';
    if (numbers.length > 1) {
        formatted += ' (' + numbers.substring(1, 4);
    }
    if (numbers.length >= 4) {
        formatted += ') ' + numbers.substring(4, 7);
    }
    if (numbers.length >= 7) {
        formatted += '-' + numbers.substring(7, 9);
    }
    if (numbers.length >= 9) {
        formatted += '-' + numbers.substring(9, 11);
    }

    return formatted;
}

// Функция для применения маски к другим полям телефона
function applyPhoneMask(inputId) {
    const input = document.getElementById(inputId);
    if (!input) return;

    input.value = '+7 ';

    input.addEventListener('input', function() {
        let numbers = this.value.replace(/\D/g, '');

        if (numbers.length > 0 && numbers[0] !== '7') {
            numbers = '7' + numbers.substring(1);
        }

        numbers = numbers.substring(0, 11);

        let formatted = '+7';
        if (numbers.length > 1) formatted += ' (' + numbers.substring(1, 4);
        if (numbers.length >= 4) formatted += ') ' + numbers.substring(4, 7);
        if (numbers.length >= 7) formatted += '-' + numbers.substring(7, 9);
        if (numbers.length >= 9) formatted += '-' + numbers.substring(9, 11);

        this.value = formatted;
    });
};
