<template>
  <div class="main-container">
    <!-- Title -->
    <div class="title-container fw-semibold text-start p-5">
      <div class="title-text">
        <div>Reizen was</div>
        <div>nog nooit zo</div>
      </div>
      <div class="title-animation">
        <TypingAnimationComponent
          :texts="['simpel', 'snel', 'leuk', 'comfortabel', 'avontuurlijk', 'verfrissend', 'flexibel', 'rustgevend']"
          :typingDelay="90"
          :erasingDelay="40"
          :delay="3000"
          :ordered="false" />
      </div>
    </div>
    <!-- Settings -->
    <div class="settings-container">
      <div class="mobile-only line mb-5 rounded m-auto"></div>
      <SettingsComponent class="mb-5" v-model="stationList" />
    </div>
  </div>
  <!-- Line -->
  <div class="line mb-5 rounded m-auto"></div>
  <!-- Table with stations -->
  <TableObjectComponent v-if="stationList?.length" :items="stationList" :attributeKeys="stationAttributesKeys" :attributeValues="stationAttributesValues" :sortable="true" :removable="false" :clickable-rows="false" />
</template>

<script>
import TypingAnimationComponent from '@/components/animations/TypingAnimationComponent';
import SettingsComponent from '@/components/SettingsComponent';
import TableObjectComponent from '@/components/helpers/TableObjectComponent';

export default {
  name: "HomeComponent",
  components: {
    TypingAnimationComponent,
    SettingsComponent,
    TableObjectComponent,
  },
  data() {
    return {
      stationList: [],// list of stations for table
      stationAttributesKeys: {
        distance: () => "Afstand",
        name: () => "Naam",
        ovBike: () => "OV Fiets",
        ovEbike: () => "OV E-bike",
        baggage: () => "Baggage Kluizen",
      },
      stationAttributesValues: {
        distance: n => n > 1000 ? (Math.round(Math.floor(n) / 100) / 10 + "km") : (Math.floor(n) + "m"),
        name: s => s,
        ovBike: b => this.boolIcon(b),
        ovEbike: b => this.boolIcon(b),
        baggage: b => this.boolIcon(b),
      }
    }
  },
  methods: {
    boolIcon(bool) {
      return `<span class="material-symbols-outlined">${bool ? "done" : "close"}</span>`;
    }
  }
};
</script>

<style scoped>
.title-container {
  font-size: 4vw;
  text-transform: uppercase;
  margin-bottom: 5vh;
  width: 50%;
}

.title-text {
  padding-left: 20%;
}

.title-text > :last-child {
  /* There should only be two title rows */
  padding-left: 4%;
}

.title-animation {
  padding-left: 27%;
  color: var(--color-secondary);
}

.main-container {
  display: flex;
}

.settings-container {
  margin-top: 5vh;
}

@media (max-width: 1000px) {
  .title-container {
    font-size: 5vw;
    width: 100%;
    margin: auto;
  }
  .main-container {
    display: block;
  }
  .settings-container {
    margin-top: 0;
  }
}
</style>