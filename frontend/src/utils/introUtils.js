
export const shouldShowIntro = () => {
    return !sessionStorage.getItem('hasVisitedFMMall');
};

export const markIntroAsViewed = () => {
    sessionStorage.setItem('hasVisitedFMMall', 'true');
};

export const resetIntro = () => {
    sessionStorage.removeItem('hasVisitedFMMall');
};


