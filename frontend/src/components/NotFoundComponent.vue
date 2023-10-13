<template>
  <!-- 404 Not found -->
  <div class="text-center pt-3">
    <h1 class="fw-semibold">404 Not Found</h1>
    <p class="lead">{{ errorMessage }}</p>
    <router-link to="/" class="btn btn-primary btn-lg">{{ buttonMessage }}</router-link>
  </div>
</template>

<script>
export default {
  name: "NotFoundComponent",
  data() {
    return {
      errorMessage: null,
      buttonMessage: null,
    };
  },
  created() {
    // Translating the text on the website
    switch(navigator.language?.split("-", 2)[0]) {
      case 'nl': {
        this.errorMessage = "Sorry, de pagina die u zoekt bestaat niet.";
        this.buttonMessage = "Klik om terug te gaan naar de home pagina";
        return;
      }
      default: {
        this.errorMessage = "Sorry, de pagina die u zoekt bestaat niet.";
        this.buttonMessage = "Click to go back to the home page";
        return;
      }
    }
  },
}
</script>

<style scoped>

</style>
