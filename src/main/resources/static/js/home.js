console.log("Home search script loaded")

document.addEventListener("DOMContentLoaded", () => {
    const searchInput = document.getElementById("player-search")
    const searchResults = document.getElementById("search-results")
    const resultsContainer = document.getElementById("results-container")
    const emptyResults = document.getElementById("empty-results")
    const loadingIndicator = document.getElementById("loading-indicator")
    const scrollHint = document.getElementById("scroll-hint")
    const debugInfo = document.getElementById("debug-info")
    const debugContent = document.getElementById("debug-content")

    let debounceTimer
    let isLoading = false
    let hasMoreResults = true
    let currentPage = 0

    // 마지막으로 로드된 플레이어 정보를 저장
    let lastPlayerId = null
    let lastMappingStatus = null
    let currentAbility = null
    let currentQuery = ""

    // 디버그 모드 활성화 (개발 중에만 true로 설정)
    const DEBUG_MODE = false

    // 초기 상태 설정 - 빈 검색 결과 표시
    searchResults.classList.remove("active")

    if (DEBUG_MODE && debugInfo) {
        debugInfo.style.display = "block"
    }

    // 디버깅 함수
    function debug(message, data = null) {
        const timestamp = new Date().toISOString().substr(11, 8)
        const logMessage = `[${timestamp}] ${message}`
        console.log(logMessage, data || "")

        if (DEBUG_MODE && debugContent) {
            const logEntry = document.createElement("div")
            logEntry.textContent = logMessage
            if (data) {
                try {
                    logEntry.textContent += ": " + JSON.stringify(data, null, 2).substring(0, 100) + "..."
                } catch (e) {
                    logEntry.textContent += ": [Object]"
                }
            }
            debugContent.appendChild(logEntry)

            // 최대 10개 로그만 유지
            while (debugContent.children.length > 10) {
                debugContent.removeChild(debugContent.firstChild)
            }

            // 스크롤을 항상 아래로 유지
            debugContent.scrollTop = debugContent.scrollHeight
        }
    }

    // Function to fetch search results
    async function fetchSearchResults(query, isLoadMore = false) {
        debug(`Fetching results for query: ${query}, isLoadMore: ${isLoadMore}, currentPage: ${currentPage}`)

        if (!query || query.length < 2) {
            resultsContainer.innerHTML = ""
            emptyResults.style.display = "block"
            scrollHint.style.display = "none"
            searchResults.classList.add("active")
            return
        }

        if (isLoading) {
            debug("Already loading, skipping request")
            return
        }

        isLoading = true
        emptyResults.style.display = "none"

        if (!isLoadMore) {
            // 새 검색이면 이전 결과와 상태 초기화
            resultsContainer.innerHTML = ""
            lastPlayerId = null
            lastMappingStatus = null
            currentAbility = null
            hasMoreResults = true
            currentPage = 0 // 페이지를 0으로 초기화
            debug("New search, resetting state")
        }

        loadingIndicator.classList.add("active")

        try {
            // API 엔드포인트 구성 - 페이지 번호를 명확히 전달
            let url = `/api/search/simple-player/${encodeURIComponent(query)}?page=${currentPage}&size=6`

            // 커서 파라미터는 페이지가 0이 아닐 때만 추가
            if (isLoadMore && currentPage > 0 && lastPlayerId !== null) {
                url += `&lastPlayerId=${lastPlayerId}`
            }

            if (isLoadMore && currentPage > 0 && lastMappingStatus !== null) {
                url += `&lastMappingStatus=${lastMappingStatus}`
            }

            if (isLoadMore && currentPage > 0 && currentAbility !== null) {
                url += `&currentAbility=${currentAbility}`
            }

            debug(`Fetching from URL: ${url}`)
            const response = await fetch(url)

            if (!response.ok) {
                throw new Error(`Network response was not ok: ${response.status}`)
            }

            const data = await response.json()
            debug("API Response:", data)

            // 응답 구조 확인 및 처리
            displaySearchResults(data, isLoadMore)
            currentQuery = query

            // 성공적으로 로드했으면 다음 페이지로 증가
            if (isLoadMore) {
                currentPage++
                debug(`Page incremented to: ${currentPage}`)
            }
        } catch (error) {
            console.error("Error fetching search results:", error)
            debug("Error:", error.message)
            // 에러 발생 시 사용자에게 알림
            if (!isLoadMore) {
                resultsContainer.innerHTML =
                    '<div class="p-4 text-center text-red-500">Error loading results. Please try again.</div>'
            }
        } finally {
            isLoading = false
            loadingIndicator.classList.remove("active")
        }
    }

    // Function to display search results
    function displaySearchResults(data, isLoadMore = false) {
        // 응답 구조 확인 및 안전하게 데이터 추출
        let players = []

        // 다양한 응답 구조 처리
        if (data && data.players && Array.isArray(data.players)) {
            players = data.players
        } else if (data && Array.isArray(data)) {
            players = data
        } else if (data && typeof data === "object") {
            // 객체에서 배열 찾기
            for (const key in data) {
                if (Array.isArray(data[key])) {
                    players = data[key]
                    break
                }
            }
        }

        debug(`Extracted ${players.length} players from response`)

        if (players.length === 0) {
            if (!isLoadMore) {
                resultsContainer.innerHTML = '<div class="p-4 text-center text-slate-500">No players found</div>'
            }
            hasMoreResults = false
            scrollHint.style.display = "none"
            searchResults.classList.add("active")
            debug("No results found or end of results")
            return
        }

        // 중복 결과 방지를 위한 기존 플레이어 ID 수집
        const existingPlayerIds = new Set()
        if (isLoadMore) {
            const existingItems = resultsContainer.querySelectorAll(".search-result-item")
            existingItems.forEach((item) => {
                const href = item.getAttribute("href")
                if (href) {
                    const playerId = href.split("/").pop()
                    if (playerId) {
                        existingPlayerIds.add(playerId)
                    }
                }
            })
            debug(`Found ${existingPlayerIds.size} existing player IDs`)
        }

        // 새로운 플레이어만 필터링
        const newPlayers = players.filter((player) => {
            const playerId = String(player.id || "")
            return !existingPlayerIds.has(playerId)
        })

        debug(`Filtered to ${newPlayers.length} new players (removed ${players.length - newPlayers.length} duplicates)`)

        if (newPlayers.length === 0 && isLoadMore) {
            hasMoreResults = false
            scrollHint.style.display = "none"
            debug("No new players to display, stopping pagination")
            return
        }

        // 기존 scroll-padding 제거 (새 결과 추가 전)
        if (isLoadMore) {
            const existingPadding = resultsContainer.querySelector(".scroll-padding")
            if (existingPadding) {
                existingPadding.remove()
            }
        }

        // 결과가 있으면 표시 - Fragment를 사용해서 한 번에 추가
        const fragment = document.createDocumentFragment()
        newPlayers.forEach((player) => {
            const resultItem = createPlayerResultItem(player)
            fragment.appendChild(resultItem)
        })

        // Fragment를 한 번에 추가하여 reflow 최소화
        resultsContainer.appendChild(fragment)

        // 마지막 플레이어 정보 저장 (무한 스크롤용) - 새로운 플레이어가 있을 때만
        if (newPlayers.length > 0) {
            const lastPlayer = newPlayers[newPlayers.length - 1]
            lastPlayerId = lastPlayer.id || null
            lastMappingStatus = lastPlayer.mappingStatus || null
            currentAbility = lastPlayer.currentAbility || null
            debug(
                `Updated cursor: lastPlayerId=${lastPlayerId}, lastMappingStatus=${lastMappingStatus}, currentAbility=${currentAbility}`,
            )
        }

        // 원본 응답의 플레이어 수가 6개 미만이면 더 이상 로드할 결과가 없음
        if (players.length < 6) {
            hasMoreResults = false
            scrollHint.style.display = "none"
            debug("Less than 6 results in response, no more to load")
        } else {
            hasMoreResults = true
            scrollHint.style.display = "block"
            debug("6 or more results in response, more may be available")
        }

        searchResults.classList.add("active")

        // 스크롤 가능한지 확인 (지연 없이 즉시 실행)
        checkScrollability()
    }

    // Function to create player result item
    function createPlayerResultItem(player) {
        const resultItem = document.createElement("a")
        resultItem.href = `/players/${player.id || ""}`
        resultItem.className =
            "search-result-item flex items-center p-3 border-b border-slate-100 last:border-b-0 text-slate-700 hover:text-blue-600 transition"

        // 안전하게 속성 접근
        const name = escapeHtml(player.name || "Unknown Player")
        const age = player.age || ""
        const teamName = escapeHtml(player.teamName || "")
        const imageUrl = player.imageUrl || "/placeholder.svg"

        resultItem.innerHTML = `
            <div class="w-10 h-10 rounded-full overflow-hidden bg-slate-200 mr-3 flex-shrink-0">
                <img src="${imageUrl}" alt="${name}" class="w-full h-full object-cover" onerror="this.src='/placeholder.svg'">
            </div>
            <div class="flex-grow">
                <div class="font-medium">${name}</div>
                <div class="text-xs text-slate-500">
                    <span class="mr-2">${age ? age + " years" : ""}</span>
                    <span>${teamName}</span>
                </div>
            </div>
        `

        return resultItem
    }

    // HTML 이스케이프 함수
    function escapeHtml(text) {
        const map = {
            "&": "&amp;",
            "<": "&lt;",
            ">": "&gt;",
            '"': "&quot;",
            "'": "&#039;",
        }
        return text.replace(/[&<>"']/g, (m) => map[m])
    }

    // 스크롤 가능 여부 확인 함수
    function checkScrollability() {
        // 즉시 실행하되, DOM 업데이트 후 한 번 더 확인
        const checkScroll = () => {
            const resultsHeight = resultsContainer.scrollHeight
            const containerHeight = searchResults.clientHeight

            debug(`Results height: ${resultsHeight}px, Container height: ${containerHeight}px`)

            // 스크롤이 필요한지 확인
            if (resultsHeight > containerHeight - 50) {
                // 로딩 인디케이터 공간 고려
                debug("Content exceeds container height, scroll should be visible")
                scrollHint.style.display = "block"
            } else {
                debug("Content does not exceed container height, no scroll needed")
                scrollHint.style.display = "none"
            }

            // 스크롤 영역이 충분히 크지 않으면 최소한의 패딩만 추가
            if (resultsContainer.children.length > 0 && resultsHeight < containerHeight - 100) {
                // 기존 패딩 제거
                const existingPadding = resultsContainer.querySelector(".scroll-padding")
                if (existingPadding) {
                    existingPadding.remove()
                }

                debug("Adding minimal padding for scroll")
                const paddingNeeded = Math.max(50, containerHeight - resultsHeight + 50) // 최소 50px
                const paddingElement = document.createElement("div")
                paddingElement.className = "scroll-padding"
                paddingElement.style.height = `${paddingNeeded}px`
                resultsContainer.appendChild(paddingElement)
            }
        }

        // 즉시 실행
        checkScroll()

        // DOM 업데이트 후 한 번 더 확인
        setTimeout(checkScroll, 10)
    }

    // 스크롤 이벤트 핸들러
    function handleScroll() {
        if (isLoading || !hasMoreResults) {
            debug("Skipping scroll handler: isLoading=" + isLoading + ", hasMoreResults=" + hasMoreResults)
            return
        }

        const scrollPosition = searchResults.scrollTop
        const scrollHeight = searchResults.scrollHeight
        const clientHeight = searchResults.clientHeight
        const scrollThreshold = scrollHeight - clientHeight - 20 // 하단에서 20px 전에 로드 시작

        debug(
            `Scroll position: ${scrollPosition}, Scroll height: ${scrollHeight}, Client height: ${clientHeight}, Threshold: ${scrollThreshold}`,
        )

        if (scrollPosition >= scrollThreshold) {
            debug("Scroll threshold reached, loading more results")
            // 다음 페이지로 증가하고 로드
            currentPage++
            fetchSearchResults(currentQuery, true)
        }
    }

    // 디바운스된 입력 핸들러
    function handleInputChange() {
        clearTimeout(debounceTimer)
        debounceTimer = setTimeout(() => {
            const query = searchInput.value.trim()
            if (query !== currentQuery) {
                debug(`Input changed to: ${query}`)
                fetchSearchResults(query)
            }
        }, 300) // 300ms debounce
    }

    // 외부 클릭 핸들러
    function handleOutsideClick(event) {
        if (!searchInput.contains(event.target) && !searchResults.contains(event.target)) {
            searchResults.classList.remove("active")
            debug("Clicked outside, hiding results")
        }
    }

    // 포커스 핸들러
    function handleInputFocus() {
        searchResults.classList.add("active")
        debug("Input focused, showing results")
        if (searchInput.value.trim().length < 2) {
            resultsContainer.innerHTML = ""
            emptyResults.style.display = "block"
            scrollHint.style.display = "none"
            debug("Input too short, showing empty state")
        } else if (currentQuery) {
            // 이미 검색 결과가 있으면 스크롤 가능 여부 다시 확인
            checkScrollability()
        }
    }

    // 이벤트 리스너 등록
    if (searchInput) {
        searchInput.addEventListener("input", handleInputChange)
        searchInput.addEventListener("focus", handleInputFocus)
    }

    if (searchResults) {
        searchResults.addEventListener("scroll", handleScroll)
    }

    document.addEventListener("click", handleOutsideClick)

    // 키보드 접근성 개선
    if (searchInput) {
        searchInput.addEventListener("keydown", (event) => {
            if (event.key === "Escape") {
                searchResults.classList.remove("active")
                searchInput.blur()
            }
        })
    }

    // 테스트용 함수 - 검색 결과 생성
    window.testSearchResults = (count) => {
        resultsContainer.innerHTML = ""
        emptyResults.style.display = "none"

        for (let i = 0; i < count; i++) {
            const mockPlayer = {
                id: i + 1,
                name: `Test Player ${i + 1}`,
                age: 25,
                teamName: "Test Team",
                imageUrl: "/placeholder.svg",
            }

            const resultItem = createPlayerResultItem(mockPlayer)
            resultsContainer.appendChild(resultItem)
        }

        searchResults.classList.add("active")
        debug(`Generated ${count} test results`)

        // 스크롤 가능 여부 확인
        checkScrollability()
    }

    // 전역 함수로 노출 (디버깅용)
    window.homeSearchDebug = {
        fetchSearchResults,
        testSearchResults: window.testSearchResults,
        getCurrentState: () => ({
            currentPage,
            currentQuery,
            isLoading,
            hasMoreResults,
            lastPlayerId,
            lastMappingStatus,
            currentAbility,
        }),
    }
})
