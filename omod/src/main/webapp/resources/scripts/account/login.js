function isUsernameValid(username) {
    return (username && username.length !== 0 && username.indexOf(' ') < 0);
}