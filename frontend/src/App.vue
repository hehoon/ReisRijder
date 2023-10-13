<template>
  <router-view></router-view>
</template>

<script>

export default {
  name: 'App',
  data() {
    return {
      apiURL: "http://localhost:8081/api",// backend api url
    };
  },
}
</script>

<style>
/* website color codes */
:root {
  --color-background: #1a1e20;
  --color-primary: #d1dee7;
  --color-secondary: #bbea00;
  --color-highlight: #809d00;
}

/* website default css */
#app {
  font-family: sans-serif;
  font-size: 1em;
  width: 100vw;
  height: 100vh;
  color: var(--color-primary);
}

body {
  background-color: var(--color-background);
}

/* classes available for all components */
.btn-primary {
  background-color: var(--color-background);
  border-color: var(--color-secondary);
  color: var(--color-secondary);
}

.btn-primary:hover {
  background-color: var(--color-highlight);
  border-color: var(--color-highlight);
  color: var(--color-secondary);
}

.btn-primary:disabled {
  background-color: var(--color-background);
  border-color: var(--color-secondary);
  color: var(--color-secondary);
}

.line {
  width: 90vw;
  height: 0.5vh;
  background-color: var(--color-primary);
}

.mobile-only {
  display: none;
}

.material-symbols-outlined {
  /* Google icons: https://fonts.google.com/icons */
  user-select: none;
  font-variation-settings:
      'FILL' 0,
      'wght' 400,
      'GRAD' 0,
      'opsz' 24
}

.clickable {
  cursor: pointer;
}

@media (max-width: 1000px) {
  .mobile-only {
    display: block;
  }
  .computer-only {
    display: none;
  }
}
</style>
