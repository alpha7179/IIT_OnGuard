🛡️ DealGuard (Project On-Guard)
"Your Chat stays Private, Your Money stays Safe."

🔒 서버 전송 없는 On-Device AI 기반 중고거래 사기 탐지 솔루션

1. 📢 Project Overview
이 프로젝트는 안드로이드 On-Device AI 기술을 활용하여, 중고거래 채팅(당근마켓 등) 중 발생하는 **사기 유도 패턴(플랫폼 이탈, 악성 URL)**을 실시간으로 감지하고 경고합니다. 모든 데이터 처리는 스마트폰 내부에서 이루어지며, 사용자 데이터는 절대 외부로 유출되지 않습니다.

2. 👥 Team Roles & Responsibilities (R&R)
우리는 3인 1팀으로 구성되며, 각자의 전문 영역을 병렬로 개발한 뒤 dev 브랜치에서 통합합니다.

🧠 AI & Model Engineering (팀장)
핵심 목표: "폰 안에서 돌아가는 가볍고 똑똑한 뇌 만들기"
주요 업무:
Model Selection: Gemma-2b-it (Google) 모델 선정 및 분석.
Quantization: MLC LLM을 활용해 모델을 안드로이드용(q4f16_1)으로 경량화/변환.
Prompt Eng: 사기 탐지에 특화된 Few-shot System Prompt 설계 및 테스트.
Deliverable: .bin 모델 파일, MLC Config JSON, 안드로이드 연동용 Helper Class.
⚙️ Android Core & Logic (백엔드 포지션)
핵심 목표: "채팅을 훔쳐보고(눈), 뇌와 UI를 연결하는 신경망 구현"
주요 업무:
Input Module: AccessibilityService를 구현하여 화면 텍스트 실시간 추출.
Filtering: 중복 데이터 방지(Debounce) 및 당근마켓 패키지 필터링.
Logic Hub: Kotlin Coroutines를 사용해 AI 추론과 URL 정규식 검사를 비동기 병렬 처리.
Fact Check: 정규식(Regex)을 이용한 악성 URL 및 전화번호 패턴 추출.
🎨 Android UI & UX (프론트엔드 포지션)
핵심 목표: "사용자에게 직관적인 경고를 띄우는 오버레이 구현"
주요 업무:
Overlay UI: WindowManager와 Jetpack Compose를 활용해 항상 떠 있는 뷰 구현.
Interaction: 위험도(안전/주의/위험)에 따라 색상과 크기가 변하는 애니메이션 적용.
Settings: 앱 감시 ON/OFF 및 권한 설정(SYSTEM_ALERT_WINDOW) 화면 구현.
Optimization: 오버레이 뷰가 다른 앱의 터치를 방해하지 않도록 플래그(FLAG_NOT_FOCUSABLE) 최적화.
3. 🛠️ Tech Stack
Category	Technology
Language	Kotlin (100%)
Android SDK	Min SDK 26 (Android 8.0)
UI Framework	Jetpack Compose, WindowManager
AI Engine	MLC LLM (Android SDK), Gemma-2b-it
Async	Kotlin Coroutines, Flow
Input	AccessibilityService API
Git	GitHub Flow (Feature Branch Strategy)
4. 🌊 Git Workflow (협업 규칙)
우리는 충돌 방지를 위해 엄격한 브랜치 전략을 사용합니다.
main: 언제나 실행 가능한 최종 배포 버전. (함부로 건드리지 않음)
dev: 개발 통합용 브랜치. (모든 기능은 여기로 모임)
feature/...: 각자 작업하는 공간.
feature/ai-model (AI 담당)
feature/accessibility (Core 담당)
feature/overlay-ui (UI 담당)
🚨 Rule: 절대 dev나 main에 직접 Push 하지 마세요. 반드시 feature 브랜치에서 작업 후 **PR(Pull Request)**을 보내주세요.

5. 📅 Roadmap (3 Weeks)
Week 1 (MVP Unit Test):
✅ AI: Gemma-2b 양자화 완료 및 로컬 테스트.
✅ Core: 당근마켓 채팅 로그 Logcat 출력 성공.
✅ UI: 화면에 빨간색 오버레이 버튼 띄우기 성공.
Week 2 (Integration):
🔄 AI 모델 안드로이드 탑재 및 연동.
🔄 텍스트 입력 → AI 판단 → UI 변경 흐름 연결.
Week 3 (Polish & Demo):
🚀 발열 및 응답 속도 최적화.
🎬 시연 영상 촬영 (사기 탐지 시나리오).
⚡ Quick Start (Setup)
Clone Project:
Bash
git clone -b dev [Repository URL]
Prerequisites:
Android Studio (Koala 이상 권장)
JDK 17 (Android Studio 내장)
실물 안드로이드 폰 (권장) 또는 에뮬레이터 (RAM 4GB 이상 설정)

📦 Gradle 동기화 (중요!)
1. Android Studio에서 프로젝트를 열면 자동으로 Gradle 동기화가 시작됩니다.
2. 하단 진행 표시줄에서 "Gradle: Executing tasks..." 또는 "Gradle: Build" 메시지를 확인하세요.
3. 동기화가 완료되면 "Gradle build finished" 메시지가 표시됩니다.
4. 만약 동기화가 실패하면:
   - File > Sync Project with Gradle Files (또는 Ctrl+Shift+O / Mac: Cmd+Shift+O)
   - File > Invalidate Caches > Invalidate and Restart
   - Android SDK가 제대로 설치되어 있는지 확인 (Tools > SDK Manager)

Permissions:
앱 실행 후 [다른 앱 위에 그리기] 권한과 **[접근성 권한]**을 반드시 수동으로 허용해야 작동합니다.
Copyright © 2026 DealGuard Team. All Rights Reserved.