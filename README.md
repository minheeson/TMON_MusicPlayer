## TMON_Music Player

---

### 작성자

* 모바일랩 인턴 손민희





### 개발 기간

* 3주 과정 (07.15 ~ 08.05)





### 개발 환경

* Android Studio 2.3.3
  * API level : 23
  * Target SDK : 25


---

## 개발 내용

#### 1. 음악 목록 화면

|              mp3 파일이 없는 경우               |                List View                 |                Card View                 |
| :--------------------------------------: | :--------------------------------------: | :--------------------------------------: |
| <img src="https://github.com/minheeson/TMON_MusicPlayer/blob/master/screenshots/screenshot_1.png"  width="200"> | <img src="https://github.com/minheeson/TMON_MusicPlayer/blob/master/screenshots/screenshot_2.png" width="200"> | <img src="https://github.com/minheeson/TMON_MusicPlayer/blob/master/screenshots/screenshot_3.png" width="200"> |

##### 1) 로컬에 저장되어 있는 mp3 파일이 없는 경우

##### 2) List View (목록 뷰)

* 음악 파일의 이미지 썸네일
* 곡명이 없을 경우, 파일 이름 사용 (확장자 제외)
  * 아티스트가 없을 경우, '알 수 없는 아티스트'
* More Option을 누르면, 옵션 메뉴 팝업
* 누르면 타일 뷰로 전환

##### 3) Card View (타일 뷰)

* 누르면 목록 뷰로 전환
* CardView 사용



| 목록 뷰                                     | 음악 일시 정지                                 | Slide Up                                 |
| ---------------------------------------- | ---------------------------------------- | ---------------------------------------- |
| <img src="https://github.com/minheeson/TMON_MusicPlayer/blob/master/screenshots/screenshot_6.png"  width="200"> | <img src="https://github.com/minheeson/TMON_MusicPlayer/blob/master/screenshots/screenshot_7.png"  width="200"> | <img src="https://github.com/minheeson/TMON_MusicPlayer/blob/master/screenshots/screenshot_8.png"  width="200"> |

##### 1) 목록 뷰 예시

* 곡을 선택하면 재생 목록에 추가 후 음악 재생
  * 중복 추가 안 됨
* 움직임 (3프레임 정도로 직접 제작 / 프레임 간격은 200ms)
  * CardView 에는 적용 안 함
* 현재 재생 중인 곡에 대한 정보
  * 검색 및 재생목록 화면에서도 보여야 함 
* 옵션에서 삭제를 선택할 경우 해당 곡이 재색중인 곡이면, 프레임 멈춤

##### 2) 음악 일시 정지

* 일시 정지 눌렀을 때, 재생 아이콘이 뜨고 해당 리스트 아이템의 움직이는 아이콘도 멈춤 

##### 3) Slide Up

* 재생 화면 Slide Up 할 수 있으
* Slide offset 에 따라 배경 그림자가 진해짐 



#### 2. 검색 결과 화면 

|                    검색                    |           검색 결과 (new Activity)           |
| :--------------------------------------: | :--------------------------------------: |
| <img src="https://github.com/minheeson/TMON_MusicPlayer/blob/master/screenshots/screenshot_4.png" width="200"> | <img src="https://github.com/minheeson/TMON_MusicPlayer/blob/master/screenshots/screenshot_5.png" width="200"> |

##### 1) 검색

* 검색 기능 : 검색어 입력 후 done action을 선택하면 검색 결과 화면으로 이동

##### 2) 검색 결과 (new Activity)

* 목록, 타일 뷰 전환 가능 

#### 3. Refresh 버튼 - 클릭하면 progress 보여주고 갱신

#### 4. 설정 아이콘 - 클릭하면 현재 화면(위젯 설정)을 보여줌

###### 		:: refresh 버튼처럼 bg color 없는 상태로 노출, Android Studio에서 asset 이미지 생성 가능 

#### 5. 검색어 클릭시 검색 상세 화면으로 이동

#### 6. 웹 페이지가 로딩되는 동안 보여짐



> 















