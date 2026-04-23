document.addEventListener("DOMContentLoaded", () => {
    const searchForm = document.getElementById("searchForm");
    const tokenContainer = document.getElementById("activeTokens");
    const resetButton = document.getElementById("resetButton");

    function getInputLabel(input) {
        if (input.type === "checkbox" || input.type === "radio") {
            const labelEl = input.labels?.[0] || input.nextElementSibling;
            if (labelEl && labelEl.tagName === "LABEL") return labelEl.textContent.trim();
        }
        const labelDiv = input.closest(".search-form")?.previousElementSibling;
        if (labelDiv && labelDiv.classList.contains("search-label")) {
            const span = labelDiv.querySelector("span");
            if (span) return span.textContent.trim();
        }
        return input.name;
    }

    function createToken(name, value, inputEl) {
        const token = document.createElement("span");
        token.className = "badge bg-primary d-flex align-items-center me-1 mb-1";
        token.style.cursor = "pointer";
        token.textContent = `${name}: ${value}`;

        const removeBtn = document.createElement("span");
        removeBtn.textContent = " ×";
        removeBtn.className = "ms-1";
        removeBtn.style.fontWeight = "bold";

        removeBtn.addEventListener("click", () => {
            if (inputEl.type === "checkbox" || inputEl.type === "radio") {
                inputEl.checked = false;
            } else {
                inputEl.value = "";
            }
            token.remove();
            inputEl.dispatchEvent(new Event("change"));
        });

        token.appendChild(removeBtn);
        tokenContainer.appendChild(token);
    }

    function refreshTokens() {
        tokenContainer.innerHTML = "";
        const inputs = Array.from(searchForm.querySelectorAll("input, select"))
            .filter(el => el.type !== "hidden" && el.name);

        inputs.forEach(input => {
            const label = getInputLabel(input);
            if ((input.type === "checkbox" || input.type === "radio") && input.checked) {
                createToken(label, "✔", input);
            } else if (input.tagName === "SELECT" && input.value) {
                const optionLabel = input.options[input.selectedIndex]?.text || input.value;
                createToken(label, optionLabel, input);
            } else if ((input.type === "text" || input.type === "date") && input.value.trim() !== "") {
                createToken(label, input.value, input);
            }
        });
    }

    searchForm.addEventListener("input", refreshTokens);
    searchForm.addEventListener("change", refreshTokens);


    resetButton.addEventListener("click", (e) => {
        e.preventDefault(); 
        const inputs = Array.from(searchForm.querySelectorAll("input, select")).filter(el => el.type !== "hidden");

        inputs.forEach(input => {
            if (input.type === "checkbox" || input.type === "radio") {
                input.checked = false;
            } else {
                input.value = "";
            }
            input.dispatchEvent(new Event("change")); 
        });

        tokenContainer.innerHTML = ""; 
    });

    refreshTokens();
});
