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
A game-trainer for developing musical ear.
</div>

<a id="screenshots"></a>
## <img src="https://readme-typing-svg.herokuapp.com?font=Lexend+Giga&size=25&pause=1000&color=CCA9DD&vCenter=true&width=435&height=25&lines=Screenshots" width="450"/>

<div align="center">
  <img src="screenshots/main-menu.png" width="90%" alt="Main Menu">
  <br><br>
  <img src="screenshots/level-select.png" width="45%" alt="Level Selection">
  <img src="screenshots/game-screen.png" width="45%" alt="Game Screen">
  <br>
  <img src="screenshots/settings.png" width="45%" alt="Settings">
  <img src="screenshots/training.png" width="45%" alt="Training Mode">
</div>

<a id="description"></a>
## <img src="https://readme-typing-svg.herokuapp.com?font=Lexend+Giga&size=25&pause=1000&color=CCA9DD&vCenter=true&width=435&height=25&lines=Description" width="450"/>

Note-Voyage is an Android application that will help you learn to play the piano. The app includes:

- **Interactive lessons** - helps improve musical ear
- **Training mode** - free play on a virtual piano
- **Sound effects** - realistic piano sounds
- **Training progress** - tracking your achievements
- **Beautiful interface** - modern design with animations

<a id="features"></a>
## <img src="https://readme-typing-svg.herokuapp.com?font=Lexend+Giga&size=25&pause=1000&color=CCA9DD&vCenter=true&width=435&height=25&lines=Features" width="450"/>

### 🎹 Core Features
- Virtual piano with realistic sounds
- Difficulty level system
- Interactive lessons with key highlighting
- Free training mode
- Sound and volume settings

### 🎵 Audio Features
- High-quality piano sounds
- Background music
- Volume control
- Sustain effect (note hold)

### 🎮 Game Elements
- Level progress
- New level unlocking

<a id="installation"></a>
## <img src="https://readme-typing-svg.herokuapp.com?font=Lexend+Giga&size=25&pause=1000&color=CCA9DD&vCenter=true&width=435&height=25&lines=Installation" width="450"/>

### Requirements
- Android 7.0 (API level 24) or higher
- Minimum 200 MB free space

### Building from Source

1. Clone the repository:
```bash
git clone https://github.com/Grossbeak/Note-Voyage
cd Note-Voyage
```

2. Open the project in Android Studio

3. Sync Gradle dependencies

4. Build the project:
```bash
./gradlew assembleDebug
```

5. Install on device:
```bash
./gradlew installDebug
```

## Usage

### First Launch
1. Launch the application
2. Select "Play" in the main menu
3. Start with the first level

### Navigation
- **Main Menu** - game mode selection
- **Level Selection** - choose a lesson for training
- **Game Screen** - interactive piano
- **Settings** - sound and other parameter settings

### Game Modes
- **Levels** - interactive lessons
- **Training** - free play

<a id="structure"></a>
## <img src="https://readme-typing-svg.herokuapp.com?font=Lexend+Giga&size=25&pause=1000&color=CCA9DD&vCenter=true&width=435&height=25&lines=Project%20Structure" width="450"/>

```
MyPianoApp/
├── app/
│   ├── src/main/
│   │   ├── java/com/grossbeak/mypianoapp/
│   │   │   ├── MainMenuActivity.java      # Main menu
│   │   │   ├── GameActivity.java          # Game screen
│   │   │   ├── LevelSelectActivity.java   # Level selection
│   │   │   ├── TrainingActivity.java      # Training mode
│   │   │   └── MusicManager.java          # Sound management
│   │   ├── res/
│   │   │   ├── layout/                    # XML layouts
│   │   │   ├── drawable/                  # Graphic resources
│   │   │   ├── values/                    # Strings, colors, styles
│   │   │   └── mipmap/                    # App icons
│   │   └── assets/
│   │       ├── note_sounds/               # Note sounds
│   │       ├── bg_music.mp3               # Background music
│   │       └── piano_keys/                # Key images
│   └── build.gradle.kts                   # Build configuration
├── gradle/
└── build.gradle.kts
```

<a id="technicaldetails"></a>
## <img src="https://readme-typing-svg.herokuapp.com?font=Lexend+Giga&size=25&pause=1000&color=CCA9DD&vCenter=true&width=435&height=25&lines=Technical%20Details" width="450"/>

### Technologies Used
- **Language**: Java
- **Minimum SDK**: API 24 (Android 7.0)
- **Target SDK**: API 34 (Android 14)
- **Architecture**: Activity-based
- **Audio**: MediaPlayer, SoundPool

### Key Components
- **Activity Lifecycle** - screen lifecycle management
- **Custom Views** - custom interface components
- **Asset Management** - resource management
- **Audio Management** - sound playback system


<a id="developmentsetup"></a>
## <img src="https://readme-typing-svg.herokuapp.com?font=Lexend+Giga&size=25&pause=1000&color=CCA9DD&vCenter=true&width=435&height=25&lines=Development%20Setup" width="450"/>

### Required Tools
- Android Studio Arctic Fox or newer
- JDK 11 or higher
- Android SDK API 24+

### Environment Variables
```bash
export ANDROID_HOME=/path/to/android/sdk
export JAVA_HOME=/path/to/jdk
```

## License

This project is licensed under GPL-3.0.

<a id="contact"></a>
## <img src="https://readme-typing-svg.herokuapp.com?font=Lexend+Giga&size=25&pause=1000&color=CCA9DD&vCenter=true&width=435&height=25&lines=Contact" width="450"/>

- **Developer**: Grossbeak
- **Email**: grossbeak.pub@gmail.com
- **GitHub**: https://github.com/Grossbeak

## ☕ Support the Project
If you want to support my work, you can send a donation to the following cryptocurrency wallets:

| Cryptocurrency | Address                                        		|
| ------------ | -------------------------------------------------- |
| **TON**      | `UQA3p4fJJDHjl0TA4u_0fWntQIQj071ZPM5_uAFM-z9OX0LS` |
| **Ethereum** | `0x81bB02abbb48BB2d6F2BeF2232722705f66D0BEd`       |
| **Bitcoin**  | `bc1quxh68yt77ruqqt03y5g7y6upp584rwy4rnahkp`       |
| **Tron**     | `TWQmHhFTfH7n5PGV9oiqScmdGMr4fkm6RF`               |

## Acknowledgments

Thank you to everyone who helped develop this application!

---

**Version**: 0.8.1  
**Last Updated**: 28.07.2025 