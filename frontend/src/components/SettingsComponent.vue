<template>
  <div class="settings-container">
    <div class="card">
      <div class="card-body">
        <!-- Title -->
        <h5 class="card-title">Instellingen</h5>
        <div class="dropdown">
          <!-- Location input -->
          <div class="input-group">
            <input v-model="streetName" type="text" class="form-control" placeholder="Straatnaam" @input="suggestStreetAddress" @focus="checkDropdown" @blur="hideDropdown(100)">
            <span class="clickable input-group-text material-symbols-outlined" @click="determineLocation">pin_drop</span>
          </div>
          <!-- Location dropdown menu -->
          <div class="dropdown-content" v-if="streetDropdown">
            <span v-for="(option, index) in streetOptions" :key="index" class="dropdown-item clickable" @click="selectStreet(option)">{{ option.name }}</span>
          </div>
        </div>
        <!-- Checkboxes -->
        <CheckboxComponent class="mt-2" :text="'OV Fiets'" v-model="ovBikeOption" />
        <CheckboxComponent class="mt-2" :text="'OV E-bike'" v-model="ovEbikeOption" />
        <CheckboxComponent class="mt-2" :text="'Bagage Kluizen'" v-model="bagageOption" />
      </div>
    </div>
    <!-- Search button -->
    <button class="btn btn-primary btn-lg mt-3" @click="loadInfo" :disabled="!streetOptions.length && !emitting">
      <span v-if="streetOptions.length">Zoek stations in de buurt</span>
      <span v-else>Vul een straatnaam in!</span>
    </button>
  </div>
</template>


<script>
import axios from "../axios-config";
import CheckboxComponent from '@/components/helpers/CheckboxComponent';

export default {
  name: "SettingsComponent",
  components: {
    CheckboxComponent,
  },
  data() {
    return {
      emitting: false,

      streetName: "",// streetName is equal to streetData.name (if streetData exists)
      streetData: {},// one of the options currently selected
      streetDropdown: false,
      streetOptions: [],// a list of streetData objects
      previousData: "",

      ovBikeOption: false,
      ovEbikeOption: false,
      bagageOption: false,
    };
  },
  methods: {
    loadInfo() {
      if (this.generateUniqueString() === this.previousData) {
        // no changes, do nothing
        return;
      }

      if (!this.streetData) {
        // if there is no street data yet, select the first available option
        this.selectStreet(this.streetOptions[0]);
      }

      // update previous data
      this.previousData = this.generateUniqueString();

      // Load and emit the list of stations matching the current parameters
      this.emitting = true;
      axios.get("/api/station/nearby", {
        params: this.generateQueryParams()
      }).then(response => this.$emit("update:modelValue", response?.data))
          .catch(() => console.error("Error fetching api stations"))
          .finally(() => this.emitting = false);
    },
    generateUniqueString() {
      // an interesting way of making sure every setting possibility is unique
      return `${this.streetName}${this.ovBikeOption}${this.ovEbikeOption}${this.bagageOption}`;
    },
    generateQueryParams() {
      // Get the parameters for requesting the stations
      return {
        limit: 5,
        lat: this.streetData.latitude,
        lon: this.streetData.longitude,
        ovbike: this.ovBikeOption,
        ovebike: this.ovEbikeOption,
        baggage: this.bagageOption,
      };
    },
    selectStreet(option) {
      // Select an option
      this.streetData = option;
      this.streetOptions = [option]
      this.streetName = option.name;
      this.hideDropdown();
    },
    determineLocation() {
      if (!navigator.geolocation) {
        alert("Geolocation is not available in this browser! :(");
        return;
      }

      // Get the current geolocation (might be inaccurate)
      navigator.geolocation.getCurrentPosition(position => {
        let lat = position.coords.latitude;
        let lon = position.coords.longitude;

        // Connect to the backend server to get the location
        axios.post("/api/address/location", {latitude: lat, longitude: lon})
            .then(response => {
              if (response?.data) {
                this.selectStreet({
                  name: response.data.name,
                  latitude: lat,
                  longitude: lon,
                });
              }
            })
            .catch(() => console.error("Error fetching api address location"));
        }, error => console.error(error.message));
    },
    suggestStreetAddress() {
      // When changing the input, set the selected option (streetData) to null
      this.streetData = null;
      // Fetch the suggested street address and update the options
      this.suggestAddress(this.streetName, this.streetOptions).then(options => {
        this.streetOptions = options;
        this.checkDropdown();
      });
    },
    async suggestAddress(text, options) {
      // Only do suggestions if at least 3 characters are present
      if (text.length < 3) {
        return [];
      }

      // Only do suggestions if at least one option does not match the current address
      // Or try to do suggestions if no options are available
      let shouldSuggest = options.length < 3;
      for (let option of options) {
        let cleanOption = option?.name?.toLowerCase().replaceAll(/[^a-z]/g, "");
        let cleanInput = text?.toLowerCase().replaceAll(/[^a-z]/g, "");
        if (!cleanOption.startsWith(cleanInput)) {
          shouldSuggest = true;
          break;
        }
      }
      if (!shouldSuggest) {
        return options;
      }

      // Connect to the backend server to get the autocompletion addresses
      try {
        let response = await axios.post("/api/address/autocomplete", {address: text});
        return response?.data ?? [];
      } catch (e) {
        console.error("Error fetching api address autocomplete")
        return [];
      }
    },
    checkDropdown() {
      // Check whether to display the dropdown
      if (this.streetName.length >= 3 && this.streetOptions) {
        this.showDropdown();
      } else {
        this.hideDropdown();
      }
    },
    showDropdown() {
      // Display the dropdown
      this.streetDropdown = true;
    },
    hideDropdown(timeout = 0) {
      // Hide the dropdown with an optional delay
      if (timeout > 0) {
        setTimeout(() => this.streetDropdown = false, timeout);
      } else {
        this.streetDropdown = false
      }
    },
  },
};
</script>

<style scoped>
.settings-container {
  width: 32vw;
  margin-left: 6vw;
}

.input-group-text {
  background-color: var(--color-highlight);
  color: var(--color-secondary);
  transition: background-color 0.2s, color 0.1s;
}

.input-group-text:hover {
  background-color: var(--color-secondary);
  color: var(--color-background);
}

.dropdown {
  position: relative;
  display: inline-block;
  width: 100%;
}

.dropdown-content {
  position: absolute;
  background-color: var(--color-primary);
  box-shadow: 0 8px 16px 0 rgba(0, 0, 0, 0.25);
  padding: 12px 16px;
  z-index: 1;
  width: 100%;
}

@media (max-width: 1000px) {
  .settings-container {
    width: 90vw;
    margin-lefT: auto;
    margin-right: auto;
  }
}
</style>