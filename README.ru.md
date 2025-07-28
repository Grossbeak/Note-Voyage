<div align="center">
	<!--<img src=".meta/logo.png" width="300px">-->
	<h1>Note Voyage</h1>
	<a href="https://github.com/Grossbeak/Note-Voyage/issues">
		<img src="https://img.shields.io/github/issues/Grossbeak/Note-Voyage?color=ffb29b&labelColor=1C2325&style=for-the-badge">
	</a>
	<a href="https://github.com/Grossbeak/Note-Voyage/stargazers">
		<img src="https://img.shields.io/github/stars/Grossbeak/Note-Voyage?color=fab387&labelColor=1C2325&style=for-the-badge">
	</a>
	<a href="./LICENSE">
		<img src="https://img.shields.io/github/license/Grossbeak/Note-Voyage?color=FCA2AA&labelColor=1C2325&style=for-the-badge">
	</a>
	<br>
	<br>
	<a href="./README.ru.md">
		<img src="https://img.shields.io/badge/README-RU-blue?color=cba6f7&labelColor=cba6f7&style=for-the-badge">
	</a>
	<a href="./README.md">
		<img src="https://img.shields.io/badge/README-ENG-blue?color=C9CBFF&labelColor=1C2325&style=for-the-badge">
	</a>
</div>

## 
<div align="center">
Игра-тренажер для развития музыкального слуха.
</div>

<a id="screenshots"></a>
## <img src="https://readme-typing-svg.herokuapp.com?font=Lexend+Giga&size=25&pause=1000&color=CCA9DD&vCenter=true&width=435&height=25&lines=Скриншоты" width="450"/>


<div align="center">
  <img src="screenshots/main-menu.png" width="90%" alt="Главное меню">
  <br><br>
  <img src="screenshots/level-select.png" width="45%" alt="Выбор уровня">
  <img src="screenshots/game-screen.png" width="45%" alt="Игровой экран">
  <br>
  <img src="screenshots/settings.png" width="45%" alt="Настройки">
  <img src="screenshots/training.png" width="45%" alt="Режим тренировки">
</div>

<a id="description"></a>
## <img src="https://readme-typing-svg.herokuapp.com?font=Lexend+Giga&size=25&pause=1000&color=CCA9DD&vCenter=true&width=435&height=25&lines=Описание" width="450"/>

Note-Voyage - это Android-приложение, которое поможет вам научиться играть на пианино. Приложение включает в себя:

- **Интерактивные уроки** - помогает улучшить музыкальный слух
- **Режим тренировки** - свободная игра на виртуальном пианино
- **Звуковые эффекты** - реалистичные звуки пианино
- **Прогресс тренировки** - отслеживание ваших достижений
- **Красивый интерфейс** - современный дизайн с анимациями

<a id="features"></a>
## <img src="https://readme-typing-svg.herokuapp.com?font=Lexend+Giga&size=25&pause=1000&color=CCA9DD&vCenter=true&width=435&height=25&lines=Функциональность" width="450"/>

### 🎹 Основные возможности
- Виртуальное пианино с реалистичными звуками
- Система уровней сложности
- Интерактивные уроки с подсветкой клавиш
- Режим свободной тренировки
- Настройки звука и громкости

### 🎵 Звуковые возможности
- Высококачественные звуки пианино
- Фоновая музыка
- Настройка громкости
- Эффект сустейна (удержание нот)

### 🎮 Игровые элементы
- Прогресс по уровням
- Разблокировка новых уровней

<a id="instalation"></a>
## <img src="https://readme-typing-svg.herokuapp.com?font=Lexend+Giga&size=25&pause=1000&color=CCA9DD&vCenter=true&width=435&height=25&lines=Установка" width="450"/>

### Требования
- Android 7.0 (API level 24) или выше
- Минимум 200 МБ свободного места

### Сборка из исходников

1. Клонируйте репозиторий:
```bash
git clone [URL_репозитория]
cd MyPianoApp
```

2. Откройте проект в Android Studio

3. Синхронизируйте Gradle зависимости

4. Соберите проект:
```bash
./gradlew assembleDebug
```

5. Установите на устройство:
```bash
./gradlew installDebug
```

<a id="Usage"></a>
## <img src="https://readme-typing-svg.herokuapp.com?font=Lexend+Giga&size=25&pause=1000&color=CCA9DD&vCenter=true&width=435&height=25&lines=Использование" width="450"/>

### Первый запуск
1. Запустите приложение
2. Выберите "Играть" в главном меню
3. Начните с первого уровня

### Навигация
- **Главное меню** - выбор режима игры
- **Выбор уровня** - выбор урока для тренировки
- **Игровой экран** - интерактивное пианино
- **Настройки** - настройка звука и других параметров

### Режимы игры
- **Уровни** - интерактивные уроки
- **Тренировка** - свободная игра

<a id="struct"></a>
## <img src="https://readme-typing-svg.herokuapp.com?font=Lexend+Giga&size=25&pause=1000&color=CCA9DD&vCenter=true&width=435&height=25&lines=Структура%20Проекта" width="450"/>

```
MyPianoApp/
├── app/
│   ├── src/main/
│   │   ├── java/com/grossbeak/mypianoapp/
│   │   │   ├── MainMenuActivity.java      # Главное меню
│   │   │   ├── GameActivity.java          # Игровой экран
│   │   │   ├── LevelSelectActivity.java   # Выбор уровня
│   │   │   ├── TrainingActivity.java      # Режим тренировки
│   │   │   └── MusicManager.java          # Управление звуком
│   │   ├── res/
│   │   │   ├── layout/                    # XML разметки
│   │   │   ├── drawable/                  # Графические ресурсы
│   │   │   ├── values/                    # Строки, цвета, стили
│   │   │   └── mipmap/                    # Иконки приложения
│   │   └── assets/
│   │       ├── note_sounds/               # Звуки нот
│   │       ├── bg_music.mp3               # Фоновая музыка
│   │       └── piano_keys/                # Изображения клавиш
│   └── build.gradle.kts                   # Конфигурация сборки
├── gradle/
└── build.gradle.kts
```

<a id="technicaldetails"></a>
## <img src="https://readme-typing-svg.herokuapp.com?font=Lexend+Giga&size=25&pause=1000&color=CCA9DD&vCenter=true&width=435&height=25&lines=Технические%20детали" width="450"/>

### Используемые технологии
- **Язык**: Java
- **Минимальный SDK**: API 24 (Android 7.0)
- **Целевой SDK**: API 34 (Android 14)
- **Архитектура**: Activity-based
- **Звук**: MediaPlayer, SoundPool

### Ключевые компоненты
- **Activity Lifecycle** - управление жизненным циклом экранов
- **Custom Views** - кастомные компоненты интерфейса
- **Asset Management** - управление ресурсами
- **Audio Management** - система воспроизведения звука


<a id="developmentsetup"></a>
## <img src="https://readme-typing-svg.herokuapp.com?font=Lexend+Giga&size=25&pause=1000&color=CCA9DD&vCenter=true&width=435&height=25&lines=Настройка%20разработки" width="450"/>

### Необходимые инструменты
- Android Studio Arctic Fox или новее
- JDK 11 или выше
- Android SDK API 24+

### Переменные окружения
```bash
export ANDROID_HOME=/path/to/android/sdk
export JAVA_HOME=/path/to/jdk
```

## Лицензия

Этот проект распространяется под лицензией GPL-3.0.

<a id="contsct"></a>
## <img src="https://readme-typing-svg.herokuapp.com?font=Lexend+Giga&size=25&pause=1000&color=CCA9DD&vCenter=true&width=435&height=25&lines=Контакты" width="450"/>

- **Разработчик**: Grossbeak
- **Email**: grossbeak.pub@gmail.com
- **GitHub**: https://github.com/Grossbeak

## ☕ Поддержать проект
Если вы хотите поддержать мою работу, вы можете отправить пожертвование на следующие криптовалютные кошельки:

| Криптовалюта | Адрес                                        		|
| ------------ | -------------------------------------------------- |
| **TON**      | `UQA3p4fJJDHjl0TA4u_0fWntQIQj071ZPM5_uAFM-z9OX0LS` |
| **Ethereum** | `0x81bB02abbb48BB2d6F2BeF2232722705f66D0BEd`       |
| **Bitcoin**  | `bc1quxh68yt77ruqqt03y5g7y6upp584rwy4rnahkp`       |
| **Tron**     | `TWQmHhFTfH7n5PGV9oiqScmdGMr4fkm6RF`               |


## Благодарности

Спасибо всем, кто помогал в разработке этого приложения!

---

**Версия**: 0.8.1  
**Последнее обновление**: 28.07.2025 
