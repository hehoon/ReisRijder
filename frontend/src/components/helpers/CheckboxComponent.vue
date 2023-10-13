<template>
  <label class="container">
    <!-- The checkbox text is displayed here -->
    {{ text }}
    <!-- The checkbox (invisible) logic is stored here -->
    <input type="checkbox" :checked="isChecked" v-model="isChecked" @change="emitChangeEvent">
    <!-- The checkbox (visible) mark is stored here -->
    <span class="checkmark"></span>
  </label>
</template>

<script>
export default {
  name: "CheckboxComponent",
  props: {
    text: String,
    modelValue: Boolean,// a prop for two-way binding
  },
  data() {
    return {
      isChecked: this.modelValue,
    }
  },
  methods: {
    emitChangeEvent() {
      this.$emit("update:modelValue", this.isChecked);
    }
  }
}
</script>

<style scoped>
.container {
  display: block;
  position: relative;
  padding-left: 1.5em;
  cursor: pointer;
  user-select: none;
}

.container:hover input[type="checkbox"] ~ .checkmark {
  /* checkmark background color when hovering over it */
  background-color: var(--color-highlight);
}

input[type="checkbox"] {
  /* never display the default checkbox
   The default checkbox exists for logic purposes only */
  display: none;
}

input[type="checkbox"]:checked ~ .checkmark:after {
  /* display checkmark when checkbox is checked */
  display: block;
  content: "";
  position: absolute;
}

.checkmark {
  position: absolute;
  top: 0.1em;
  left: 0;
  height: 1.1em;
  width: 1.1em;
  background-color: var(--color-primary);
  border-radius: 0.25em;
  outline: 1px solid var(--color-background);
}

.checkmark:after {
  /* display checkmark (without hovering over it) */
  left: 0.3em;
  top: 0.11em;
  height: 0.7em;
  width: 0.5em;
  border: solid var(--color-background);
  border-width: 0 0.2em 0.2em 0;
  transform: rotate(45deg);
}

.container:hover .checkmark:after {
  /* display checkmark when hovering over it */
  left: 0.3em;
  top: 0.11em;
  height: 0.7em;
  width: 0.5em;
  border: solid var(--color-secondary);
  border-width: 0 0.2em 0.2em 0;
  transform: rotate(45deg);
}
</style>