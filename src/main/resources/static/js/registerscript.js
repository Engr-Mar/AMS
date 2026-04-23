document.addEventListener("DOMContentLoaded", function () {

    const tabs = document.querySelectorAll(".notebook-tab");
    const sections = document.querySelectorAll(".notebook-section");
    const nextButton = document.getElementById("nextTabButton");
    const prevButton = document.getElementById("prevTabButton");
    const submitButton = document.getElementById("submitButton");
    const cancelButton = document.getElementById("cancelButton");
    const actionInput = document.getElementById("action");

    function deactivateAll() {
        tabs.forEach(tab => tab.classList.remove("active"));
        sections.forEach(section => {
            section.classList.remove("active");
            section.style.display = "none";
        });
    }

    function activateTab(index) {
        if (index < 0 || index >= tabs.length) return;

        deactivateAll();
        tabs[index].classList.add("active");
        sections[index].style.display = "flex";

        if (prevButton)
            prevButton.style.display = (index === 0) ? "none" : "inline-block";

        if (nextButton)
            nextButton.style.display = (index === tabs.length - 1) ? "none" : "inline-block";

        if (submitButton)
            submitButton.style.display = (index === tabs.length - 1) ? "inline-block" : "none";
    }

    tabs.forEach((tab, idx) =>
        tab.addEventListener("click", () => activateTab(idx))
    );

    if (tabs.length && sections.length) activateTab(0);

    if (nextButton) {
        nextButton.addEventListener("click", e => {
            e.preventDefault();
            activateTab(
                Array.from(tabs).findIndex(tab => tab.classList.contains("active")) + 1
            );
        });
    }

    if (prevButton) {
        prevButton.addEventListener("click", e => {
            e.preventDefault();
            activateTab(
                Array.from(tabs).findIndex(tab => tab.classList.contains("active")) - 1
            );
        });
    }

    if (cancelButton) {
        cancelButton.addEventListener("click", function (e) {
            e.preventDefault();
            const returnUrlInput = document.querySelector('input[name="returnUrl"], input[name="redirectUrl"]');
            const returnUrl = returnUrlInput ? returnUrlInput.value : '/search';
            const delay = 1000;
            setTimeout(() => window.location.href = returnUrl, delay);
        });
    }

    // ======= Initialize TomSelect =======
    document.querySelectorAll(".ts-wrapper").forEach(el => {
        new TomSelect(el, {
            create: true,
            persist: false,
            maxItems: 1,
            openOnFocus: true,
            dropdownParent: 'body',
            plugins: ['clear_button'],

            onDropdownOpen() {
            this.positionDropdown();
            }
        });
    });

    // Grab TomSelect instances correctly
    const cpu1 = document.getElementById('cpu1')?.tomselect || TomSelect.getInstance(document.getElementById('cpu1'));
    const cpu2 = document.getElementById('cpu2')?.tomselect || TomSelect.getInstance(document.getElementById('cpu2'));
    const cpu3 = document.getElementById('cpu3')?.tomselect || TomSelect.getInstance(document.getElementById('cpu3'));

    // ======= Cascading =======
    // CPU manufacturer -> generation
    cpu1?.on('change', function (manufacturer) {

        // reset cpu2
        cpu2.clear(true);
        cpu2.clearOptions();
        cpu2.addOption({ value: '', text: '', disabled: true });

        // reset cpu3
        cpu3.clear(true);
        cpu3.clearOptions();
        cpu3.addOption({ value: '', text: '', disabled: true });

        // ---------- GENERATIONS ----------
        let genUrl = '/api/register/generation';

        if (manufacturer)
            genUrl += `?manufacturer=${encodeURIComponent(manufacturer)}`;

        fetch(genUrl)
            .then(res => res.json())
            .then(data => {
                data.forEach(item =>
                    cpu2.addOption({ value: item, text: item })
                );
                cpu2.refreshOptions(false);
            })
            .catch(err => console.error("Error fetching generations:", err));


        // ---------- MODELS ----------
        let modelUrl = '/api/register/models';

        if (manufacturer)
            modelUrl += `?manufacturer=${encodeURIComponent(manufacturer)}`;

        fetch(modelUrl)
            .then(res => res.json())
            .then(data => {
                data.forEach(item =>
                    cpu3.addOption({ value: item, text: item })
                );
                cpu3.refreshOptions(false);
            })
            .catch(err => console.error("Error fetching models:", err));

    });

    // When generation changes, filter models further by manufacturer + generation
    cpu2?.on('change', function (generation) {

        const manufacturer = cpu1?.getValue();

        // reset cpu3
        cpu3.clear(true);
        cpu3.clearOptions();
        cpu3.addOption({ value: '', text: '', disabled: true });


        // build query dynamically
        let params = [];

        if (manufacturer)
            params.push(`manufacturer=${encodeURIComponent(manufacturer)}`);

        if (generation)
            params.push(`generation=${encodeURIComponent(generation)}`);

        let url = '/api/register/models';

        if (params.length > 0)
            url += '?' + params.join('&');

        // fetch models
        fetch(url)
            .then(res => res.json())
            .then(data => {
                data.forEach(item =>
                    cpu3.addOption({ value: item, text: item })
                );

                cpu3.refreshOptions(false);
            })
            .catch(err => console.error("Error fetching models:", err));
    });
    // ======= Navigation highlight =======
    const currentPath = window.location.pathname;
    const navLinks = document.querySelectorAll('nav .nav-link');
    navLinks.forEach(link => {
        link.classList.remove('active');
        const href = link.getAttribute('href');
        if (!href) return;
        if (href === '/search' && currentPath.startsWith('/search')) link.classList.add('active');
        if (href === '/registration' && (currentPath.startsWith('/registration') || currentPath.startsWith('/update'))) link.classList.add('active');
        if (href === '/profile' && currentPath.startsWith('/profile')) link.classList.add('active');
        if (href === '/register' && currentPath.startsWith('/register')) link.classList.add('active');
    });

    // ======= Toast handling =======
    const toastEl = document.querySelector('.toast');
    if (toastEl) {
        const returnUrlInput = document.querySelector('input[name="returnUrl"], input[name="redirectUrl"]');
        const returnUrl = returnUrlInput ? returnUrlInput.value : null;
        const isSuccess = toastEl.classList.contains('bg-success');
        const delay = isSuccess ? 2000 : 0;

        const bsToast = new bootstrap.Toast(toastEl, { delay: delay, autohide: isSuccess });
        bsToast.show();

        if (actionInput) {
            if (isSuccess && actionInput.value === "update" && returnUrl)
                setTimeout(() => window.location.href = returnUrl, delay);
            if (isSuccess && actionInput.value === "register")
                setTimeout(() => window.location.href = '/search', delay);
        }
    }

    // ======= Input highlighting =======
    const messEl = document.getElementById("mess");
    const computerNameInput = document.getElementById("computerName");
    if (messEl && computerNameInput) {
        const mess = messEl.value;
        if (mess && mess.trim() !== "") {
            computerNameInput.style.border = "2px solid red";
            computerNameInput.title = mess;
        }
    }

});