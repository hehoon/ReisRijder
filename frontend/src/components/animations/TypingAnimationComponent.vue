<template>
  <!-- The text being typed is displayed here -->
  <span>{{ textValue }}</span>
  <!-- The blinking cursor is displayed here -->
  <span class="blinking-cursor">&#x7C;</span>
</template>

<script>
export default {
  name: "TypingAnimationComponent",
  data: () => {
    return {
      // The text being typed
      textValue: "",
      // Index to keep track of the current character being typed
      index: 0,
      // Index to keep track of the current text block being typed
      typingIndex: 0
    };
  },
  props: {
    // The array of texts
    texts: Array,
    // Delay before typing a character
    typingDelay: Number,
    // Delay before erasing a character
    erasingDelay: Number,
    // Delay before switching to a new text
    delay: Number,
    // Whether to type texts in order or randomly
    ordered: Boolean
  },
  created() {
    if (!this.ordered) {
      // Initialize a random index when texts are unordered
      this.index = Math.floor(Math.random() * this.texts.length);
    }
    // Start typing animation (only if there are texts to display in the first place)
    if (this.texts?.length) {
      setTimeout(this.typeText, this.delay);
    }
  },
  methods: {
    typeText() {
      // Check if there are characters left to type in the current text
      if (this.typingIndex < this.texts[this.index].length) {
        // Add the next character to the textValue and increment the typingIndex
        this.textValue += this.texts[this.index].charAt(this.typingIndex++);
        // Schedule the next character typing with a delay
        setTimeout(this.typeText, this.typingDelay);
        return;
      }

      // When all characters are typed, schedule the erasing of text
      setTimeout(this.eraseText, this.delay);
    },
    eraseText() {
      // Check if there are characters left to erase
      if (this.typingIndex > 0) {
        // Remove the last character from textValue and decrement typingIndex
        this.textValue = this.texts[this.index].substring(0, --this.typingIndex);
        // Schedule the next character erasing with a delay
        setTimeout(this.eraseText, this.erasingDelay);
        return;
      }

      // After erasing, determine the next text to type
      if (this.ordered) {
        // If in ordered mode, move to the next text in sequence
        this.index = (this.index + 1) % this.texts.length;
      } else if (this.texts.length >= 2) {
        // If in random mode and there are at least 2 texts, choose a random text index different from the current one
        let oldIndex = this.index;
        do {
          this.index = Math.floor(Math.random() * this.texts.length);
        } while (this.index === oldIndex);
      }

      // Schedule the next typing of a text after erasing with a delay
      setTimeout(this.typeText, this.typingDelay + this.delay / 2);
    }
  }
};
</script>

<style scoped>
.blinking-cursor {
  /* CSS animation for the blinking cursor */
  animation: 1s blink step-end infinite;
  user-select: none;
}

@media (prefers-reduced-motion) {
  /* Styles to apply if a user's device settings are set to reduced motion */
  .blinking-cursor {
    /* Disables the blinking animation if reduced motion is preferred */
    animation: none;
  }
}
@keyframes blink {
  0% {
    opacity: 0;
  }
  50% {
    opacity: 1;
  }
}
</style>