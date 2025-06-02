// console.log("Player search script loaded")
//
// // 섹션 토글 함수 - 전역 함수로 선언
// function toggleSection(sectionId) {
//     console.log("Toggle section called:", sectionId)
//     const section = document.getElementById(sectionId)
//     if (section) {
//         section.classList.toggle("collapsed")
//         console.log("Section toggled:", sectionId, "collapsed:", section.classList.contains("collapsed"))
//     } else {
//         console.error("Section not found:", sectionId)
//     }
// }
//
// // 포지션 토글 함수 - 전역 함수로 선언
// function togglePosition(position) {
//     console.log("Toggle position called:", position)
//     const inputField = document.getElementById(position)
//     const dot = document.querySelector(`[data-position="${position}"]`)
//
//     if (inputField) {
//         if (inputField.value === "10") {
//             inputField.value = ""
//             if (dot) dot.classList.remove("selected")
//             console.log("Removed value for position:", position)
//         } else {
//             inputField.value = "10"
//             if (dot) dot.classList.add("selected")
//             console.log("Set value 10 for position:", position)
//         }
//     } else {
//         console.error("Input field not found for position:", position)
//     }
// }
//
// // 정렬 업데이트 함수 - 전역 함수로 선언
// function updateSort(sortValue) {
//     console.log("Updating sort to:", sortValue)
//     document.getElementById("sortField").value = sortValue
//     document.querySelector("form").submit()
// }
//
// // 폼 리셋 함수 - 전역 함수로 선언
// function resetForm() {
//     console.log("Reset button clicked")
//
//     const form = document.getElementById("searchForm")
//     if (form) {
//         // 모든 텍스트 및 숫자 입력 필드 초기화
//         const inputs = form.querySelectorAll('input[type="text"], input[type="number"]')
//         inputs.forEach((input) => {
//             input.value = ""
//         })
//
//         // 셀렉트 박스 초기값으로 설정
//         const ageMinSelect = document.getElementById("ageMin")
//         if (ageMinSelect) {
//             ageMinSelect.value = "15"
//         }
//
//         const ageMaxSelect = document.getElementById("ageMax")
//         if (ageMaxSelect) {
//             ageMaxSelect.value = "50"
//         }
//
//         // 페이지 번호 초기화
//         const pageInput = document.getElementById("pageField")
//         if (pageInput) {
//             pageInput.value = "0"
//         }
//
//         // 정렬 초기화
//         const sortField = document.getElementById("sortField")
//         if (sortField) {
//             sortField.value = ""
//         }
//
//         // 정렬 셀렉트 박스도 초기화
//         const sortBySelect = document.getElementById("sortBy")
//         if (sortBySelect) {
//             sortBySelect.value = "name"
//         }
//
//         // 포지션 맵 초기화
//         const positionDots = document.querySelectorAll(".position-clickable")
//         positionDots.forEach((dot) => {
//             dot.classList.remove("selected")
//         })
//
//         console.log("Form fields reset successfully")
//     } else {
//         console.error("Form not found")
//     }
// }
//
// // DOM 로드 후 초기화
// document.addEventListener("DOMContentLoaded", () => {
//     console.log("DOM Content Loaded - initializing...")
//
//     // 포지션 도트 초기 상태 업데이트
//     function updatePositionDots() {
//         const positions = ["GK", "LB", "CB", "RB", "LWB", "RWB", "DM", "LM", "CM", "RM", "LAM", "CAM", "RAM", "ST"]
//         positions.forEach((position) => {
//             const inputField = document.getElementById(position)
//             const dot = document.querySelector(`[data-position="${position}"]`)
//
//             if (inputField && dot) {
//                 if (inputField.value && Number.parseInt(inputField.value) >= 10) {
//                     dot.classList.add("selected")
//                 } else {
//                     dot.classList.remove("selected")
//                 }
//             }
//         })
//     }
//
//     // 초기 포지션 도트 상태 설정
//     updatePositionDots()
//
//     // 입력 필드 변경 시 포지션 도트 업데이트
//     const positionInputs = document.querySelectorAll(
//         'input[id="GK"], input[id="LB"], input[id="CB"], input[id="RB"], input[id="LWB"], input[id="RWB"], input[id="DM"], input[id="LM"], input[id="CM"], input[id="RM"], input[id="LAM"], input[id="CAM"], input[id="RAM"], input[id="ST"]',
//     )
//     positionInputs.forEach((input) => {
//         input.addEventListener("input", updatePositionDots)
//         input.addEventListener("change", updatePositionDots)
//     })
//
//     // 폼 유효성 검사
//     const form = document.querySelector("form")
//     if (form) {
//         form.addEventListener("submit", (e) => {
//             const ageMin = document.getElementById("ageMin")
//             const ageMax = document.getElementById("ageMax")
//
//             if (ageMin && ageMax && ageMin.value && ageMax.value) {
//                 if (Number.parseInt(ageMin.value) > Number.parseInt(ageMax.value)) {
//                     e.preventDefault()
//                     alert("Min Age must be less than or equal to Max Age")
//                     return false
//                 }
//             }
//         })
//     }
//
//     console.log("Initialization complete")
// })
