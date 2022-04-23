setTimeout(function () {
    let usernameError = document.getElementById("username_errors");
    let passwordError = document.getElementById("password_errors");
    let repeatError = document.getElementById("repeat_errors");

    let usernameInput = document.getElementById("username");
    let passwordInput = document.getElementById("password");
    let repeatInput = document.getElementById("repeat");
    let button = document.getElementById("button");

    let isUsernameCorrect = checkLength(usernameInput, usernameError);
    let isPasswordCorrect = checkLength(passwordInput, passwordError);
    let isRepeatCorrect = checkRepeat(passwordInput, repeatInput, repeatError);
    showButton(isUsernameCorrect, isPasswordCorrect, isRepeatCorrect, button);

    usernameInput.addEventListener("keyup", function () {
        isUsernameCorrect = checkLength(usernameInput, usernameError);
        showButton(isUsernameCorrect, isPasswordCorrect, isRepeatCorrect, button);
    });


    passwordInput.addEventListener("keyup", function () {
        isPasswordCorrect = checkLength(passwordInput, passwordError);
        isRepeatCorrect = checkRepeat(passwordInput, repeatInput, repeatError);
        showButton(isUsernameCorrect, isPasswordCorrect, isRepeatCorrect, button);
    });


    repeatInput.addEventListener("keyup", function () {
        isRepeatCorrect = checkRepeat(passwordInput, repeatInput, repeatError);
        showButton(isUsernameCorrect, isPasswordCorrect, isRepeatCorrect, button);
    });
}, 500);

let checkLength = function (input, error) {
    if (input.value.length < 8 || input.value.length > 32) {
        error.innerHTML = "Length from 8 to 32 symbols";
        return false;
    } else {
        error.innerHTML = "";
        return true;
    }
}

let checkRepeat = function (input, inputToCompare, error) {
    if (input.value !== inputToCompare.value) {
        error.innerHTML = "Password mismatch";
        return false;
    } else {
        error.innerHTML = "";
        return true;
    }
}

let showButton = function (flag1, flag2, flag3, button) {
    button.disabled = !(flag1 && flag2 && flag3);
}