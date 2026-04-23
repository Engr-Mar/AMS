document.addEventListener("DOMContentLoaded", () => {
    const currentPath = window.location.pathname;
    const navLinks = document.querySelectorAll('nav .nav-link');

    navLinks.forEach(link => {
        link.classList.remove('active');
        const href = link.getAttribute('href');
        if (!href) return;

        if (href === '/search' && currentPath.startsWith('/search')) {
            link.classList.add('active');
        }

        if (
            href === '/registration' &&
            (currentPath.startsWith('/registration') || currentPath.startsWith('/update'))
        ) {
            link.classList.add('active');
        }
	    if (href === '/profile' && currentPath.startsWith('/profile')) {
            link.classList.add('active');
        }
        if (href === '/register' && currentPath.startsWith('/register')) {
            link.classList.add('active');
        }
    });
});
