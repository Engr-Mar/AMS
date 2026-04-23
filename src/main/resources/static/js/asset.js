document.addEventListener("DOMContentLoaded", () => {
    const table = document.getElementById("resultsTable");
    const tbody = table?.querySelector("tbody");
    const rowsPerPageSelect = document.getElementById("rowsPerPage");
    const pagination = document.getElementById("pagination");
    const sortSelect = document.getElementById("sortSelect");
    const scroller = document.querySelector(".result-panel .table-responsive");
    const searchForm = document.getElementById("searchForm");
    const resetButton = document.getElementById("resetButton");
    const fullUrlInput = document.getElementById("fullUrl");

    if (!tbody || !searchForm) return;

    let rows = [];
    const pageInput = document.getElementById("page");
    const currpageInput = document.getElementById("currentPage");
    const maxInput = document.getElementById("maxResults");
    let currentPage = pageInput ? parseInt(pageInput.value, 10) || 1 : 1;

    const getRowsPerPage = () => parseInt(rowsPerPageSelect?.value || "20", 10);
    const getPageCount = () => Math.max(1, Math.ceil(rows.length / getRowsPerPage()));
    const hasNoResults = () => tbody.querySelector("td[colspan]") !== null;

    function collectRows() {
        rows = Array.from(tbody.querySelectorAll("tr")).filter(tr => !tr.querySelector("td[colspan]"));
    }

    function showPage(page) {
        currentPage = page;
        const rpp = getRowsPerPage();
        const start = (page - 1) * rpp;
        const end = start + rpp;

        rows.forEach((row, index) => {
            row.style.display = index >= start && index < end ? "" : "none";
        });

        updateActivePage();
        if (scroller) scroller.scrollTop = 0;
        if (currpageInput) currpageInput.value = currentPage;
        if (maxInput) maxInput.value = "0";

        updateCurrentPagesInputs(currentPage);
        updateLinksWithPage(currentPage);
    }

    function updateCurrentPagesInputs(page) {
        document.querySelectorAll(".updateForm").forEach(form => {
            if (form.closest("tr").style.display !== "none") {
                const input = form.querySelector(".currentPages");
                if (input) input.value = page;
            }
        });
    }

    function updateLinksWithPage(page) {
        const pageNumber = parseInt(page, 10) || 1;
        document.querySelectorAll("a.update-link").forEach(link => {
            const baseUrl = link.dataset.baseUrl;
            const returnUrl = fullUrlInput ? fullUrlInput.value : null;
            if (!baseUrl) return;
            const separator = baseUrl.includes("?") ? "&" : "?";
            link.href = `${baseUrl}${separator}returnUrl=${returnUrl}&page=${pageNumber}&max=1`;
        });
    }

    function updateActivePage() {
        pagination.querySelectorAll(".page-item").forEach(li => li.classList.remove("active"));
        const active = pagination.querySelector(`a[data-page="${currentPage}"]`);
        active?.parentElement.classList.add("active");
    }

    function createPageItem(label, page) {
        const li = document.createElement("li");
        li.className = "page-item";
        const a = document.createElement("a");
        a.href = "#";
        a.className = "page-link";
        a.textContent = label;
        a.dataset.page = page;

        a.addEventListener("click", e => {
            e.preventDefault();
            const pageCount = getPageCount();
            let target = page;
            if (page === "prev") target = Math.max(1, currentPage - 1);
            if (page === "next") target = Math.min(pageCount, currentPage + 1);
            if (page === "first") target = 1;
            if (page === "last") target = pageCount;
            showPage(Number(target));
        });

        li.appendChild(a);
        return li;
    }

    function renderPagination() {
        pagination.innerHTML = "";
        if (rows.length === 0) {
            pagination.style.display = "none";
            return;
        }
        pagination.style.display = "";
        const pages = getPageCount();
        pagination.append(createPageItem("«", "first"));
        pagination.append(createPageItem("‹", "prev"));
        for (let i = 1; i <= pages; i++) {
            const li = createPageItem(i, i);
            if (i === currentPage) li.classList.add("active");
            pagination.appendChild(li);
        }
        pagination.append(createPageItem("›", "next"));
        pagination.append(createPageItem("»", "last"));
    }

    function initPagination() {
        collectRows();
        if (hasNoResults()) {
            pagination.style.display = "none";
            return;
        }
        renderPagination();
        showPage(currentPage);
    }

    sortSelect?.addEventListener("change", () => {
        const formData = new FormData(searchForm);
        formData.set("sort", sortSelect.value);
        const params = new URLSearchParams(formData).toString();
        fetch(`/search/sort?${params}`, { method: "GET", headers: { "X-Requested-With": "XMLHttpRequest" } })
            .then(res => res.text())
            .then(html => {
                tbody.innerHTML = html;
                collectRows();
                renderPagination();
                showPage(1);
                lockColumnWidths();
                addColumnResizers();
            })
            .catch(err => console.error("Sort failed:", err));

        document.querySelectorAll(".updateForm").forEach(form => {
            if (form.closest("tr").style.display !== "none") {
                const input = form.querySelector(".currentSort");
                if (input) input.value = sortSelect.value;
            }
        });
    });

    resetButton?.addEventListener("click", e => {
        e.preventDefault();
        searchForm.querySelectorAll("input[type=checkbox]").forEach(cb => cb.checked = false);
        searchForm.querySelectorAll("input[type=text], input[type=number], input[type=date]").forEach(input => input.value = "");
        searchForm.querySelectorAll("select").forEach(select => select.selectedIndex = 0);
        searchForm.querySelectorAll("textarea").forEach(textarea => textarea.value = "");
        searchForm.querySelectorAll("input[type=hidden]").forEach(input => {
            if (input.name !== "_csrf") input.value = "";
        });
    });

    document.querySelectorAll(".updateForm").forEach(form => {
        form.addEventListener("submit", e => {
            const input = form.querySelector(".currentPages");
            const sortInput = form.querySelector(".currentSort");
            if (input) input.value = currentPage;
            if (sortInput) sortInput.value = sortSelect.value;
        });
    });
    function lockColumnWidths() {
        const headers = table.querySelectorAll("thead th");

        headers.forEach((th, i) => {
            const width = th.offsetWidth;

            if (!th.dataset.originalWidth) {
                th.dataset.originalWidth = width;
            }

            th.style.width = width + "px";
            th.style.minWidth = width + "px";
            th.style.maxWidth = width + "px";

            table.querySelectorAll(`tbody td:nth-child(${i + 1})`).forEach(td => {
                td.style.width = width + "px";
                td.style.minWidth = width + "px";
                td.style.maxWidth = width + "px";
            });
        });
    }

    function alignColumnHeaderBar() {
        const headerBar = document.querySelector(".col-toggle-btn-disabled");
        const table = document.getElementById("resultsTable");

        if (!headerBar || !table) return;

        const stickyCols = table.querySelectorAll("thead th:nth-child(-n+3)");

        let totalWidth = 0;
        stickyCols.forEach(th => {
            if (!th.classList.contains("col-hidden")) {
                totalWidth += th.offsetWidth;
            }
        });
        headerBar.style.width = "";
        headerBar.style.width = totalWidth + "px";
    }

    initPagination();
    lockColumnWidths();
    alignColumnHeaderBar();
    addColumnResizers();

    rowsPerPageSelect.addEventListener("change", () => {
        currentPage = 1;
        initPagination();
    });

    const currentPath = window.location.pathname;
    document.querySelectorAll('nav .nav-link').forEach(link => {
        link.classList.remove('active');
        const href = link.getAttribute('href');
        if (!href) return;
        if (href === '/search' && currentPath.startsWith('/search')) link.classList.add('active');
        if (href === '/registration' && (currentPath.startsWith('/registration') || currentPath.startsWith('/update'))) link.classList.add('active');
        if (href === '/profile' && currentPath.startsWith('/profile')) link.classList.add('active');
    });

    const inputs = document.querySelectorAll("#searchForm input[type=text], #searchForm input[type=date], #searchForm select");

    function toggleHighlight(input) {
        if ((input.type === "text" || input.type === "date") && input.value.trim() !== "") input.classList.add("input-has-value");
        else if (input.tagName === "SELECT" && input.value) input.classList.add("input-has-value");
        else input.classList.remove("input-has-value");
    }

    inputs.forEach(input => toggleHighlight(input));
    inputs.forEach(input => {
        input.addEventListener("input", () => toggleHighlight(input));
        input.addEventListener("change", () => toggleHighlight(input));
    });


    const getIcon = (collapseEl) => {
    const trigger = document.querySelector(`[data-bs-target="#${collapseEl.id}"]`);
        return trigger?.querySelector('.toggle-icon');
    };

    const setIcon = (collapseEl, isOpen) => {
        const icon = getIcon(collapseEl);
        if (!icon) return;

        icon.classList.toggle('bi-plus-lg', !isOpen);
        icon.classList.toggle('bi-dash-lg', isOpen);
    };

    // Handle collapse events
    document.querySelectorAll('.collapse').forEach(collapseEl => {

        collapseEl.addEventListener('show.bs.collapse', () => {
            setIcon(collapseEl, true);
        });

        collapseEl.addEventListener('hide.bs.collapse', () => {
            setIcon(collapseEl, false);
        });

    });

    document.querySelectorAll('.collapse').forEach(collapseEl => {
        const isOpen = collapseEl.classList.contains('show'); // check if default open
        setIcon(collapseEl, isOpen);
    });

    window.addEventListener("resize", alignColumnHeaderBar);

    document.querySelectorAll(".col-toggle-btn.active").forEach(btn => {
        const onclickAttr = btn.getAttribute("onclick");

        if (!onclickAttr) return;

        // Extract group name from: toggleColumnGroup('col-det', this)
        const match = onclickAttr.match(/'([^']+)'/);
        if (!match) return;

        const groupClass = match[1];

        toggleColumnGroup(groupClass, btn);
        toggleColumnGroup(groupClass, btn);
    });

});

function toggleColumnGroup(groupClass, btn) {

    const table = document.getElementById("resultsTable");
    const elements = document.querySelectorAll("#resultsTable ." + groupClass);

    if (!elements.length) return;

    // store original width once
    if (!table.dataset.originalWidth) {
        table.dataset.originalWidth = table.offsetWidth;
    }

    const isHidden = elements[0].classList.contains("col-hidden");

    // calculate width of the column group
    let groupWidth = 0;
    document.querySelectorAll("#resultsTable thead th." + groupClass).forEach(th => {
        groupWidth += th.offsetWidth;
    });

    let tableWidth = table.offsetWidth;

    if (!isHidden) {
        // hide column group
        table.style.width = (tableWidth - groupWidth) + "px";
    } else {
        // show column group
        table.style.width = (tableWidth + groupWidth) + "px";

        const stillHidden = table.querySelector("th.col-hidden");
        if (!stillHidden) {
            table.style.width = table.dataset.originalWidth + "px";
        }
    }

    elements.forEach(el => {
        el.classList.toggle("col-hidden");
    });

    // toggle button active style
    if (btn) {
        btn.classList.toggle("active");
    }

    const headerBar = document.querySelector(".col-toggle-btn-disabled");

    if (!headerBar || !table) return;

    const stickyCols = table.querySelectorAll("thead th:nth-child(-n+3)");

    let totalWidth = 0;
    stickyCols.forEach(th => {
        if (!th.classList.contains("col-hidden")) {
            totalWidth += th.offsetWidth;
        }
    });

    headerBar.style.width = totalWidth + "px";
}

function addColumnResizers() {
    const table = document.getElementById("resultsTable");
    const headers = table.querySelectorAll("thead th");

    headers.forEach((th, index) => {

        // ❌ Skip hidden columns
        if (th.classList.contains("col-hidden")) return;

        // ❌ Skip if already has resizer
        if (th.querySelector(".resizer")) return;

        // Create resizer
        const resizer = document.createElement("div");
        resizer.className = "resizer";
        th.appendChild(resizer);

        let startX = 0;
        let startWidth = 0;

        resizer.addEventListener("mousedown", (e) => {
            e.preventDefault();

            startX = e.pageX;
            startWidth = th.offsetWidth;

            function onMouseMove(e) {
                let newWidth = startWidth + (e.pageX - startX);

                // ✅ Minimum width protection
                const minWidth = th.dataset.originalWidth || 50;
                newWidth = Math.max(minWidth, newWidth);

                // Apply to header
                th.style.width = newWidth + "px";
                th.style.minWidth = newWidth + "px";
                th.style.maxWidth = newWidth + "px";

                // Apply to all body cells in column
                table.querySelectorAll(`tbody td:nth-child(${index + 1})`)
                    .forEach(td => {
                        td.style.width = newWidth + "px";
                        td.style.minWidth = newWidth + "px";
                        td.style.maxWidth = newWidth + "px";
                    });
            }

            function onMouseUp() {
                document.removeEventListener("mousemove", onMouseMove);
                document.removeEventListener("mouseup", onMouseUp);

                // ✅ Keep sticky alignment
                if (typeof alignColumnHeaderBar === "function") {
                    alignColumnHeaderBar();
                }
            }

            document.addEventListener("mousemove", onMouseMove);
            document.addEventListener("mouseup", onMouseUp);
        });

        // ✅ DOUBLE CLICK on the TH (not resizer) to reset
        th.addEventListener("dblclick", () => {
            const originalWidth = th.dataset.originalWidth;
            if (!originalWidth) return;

            // Reset header
            th.style.width = originalWidth + "px";
            th.style.minWidth = originalWidth + "px";
            th.style.maxWidth = originalWidth + "px";

            // Reset body cells
            table.querySelectorAll(`tbody td:nth-child(${index + 1})`)
                .forEach(td => {
                    td.style.width = originalWidth + "px";
                    td.style.minWidth = originalWidth + "px";
                    td.style.maxWidth = originalWidth + "px";
                });

            // Keep layout aligned
            if (typeof alignColumnHeaderBar === "function") {
                alignColumnHeaderBar();
            }
        });
    });
}
