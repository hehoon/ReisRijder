<template>
  <table>
    <thead>
    <tr>
      <th v-for="attr of Object.keys(attributeValues)" :key="attr" @click="sort(attr)" :class="{'clickable': sortable}">
        <div class="d-flex justify-content-center">
          <span v-html="displayAttributeName(attr)" class="d-flex justify-content-center"></span>
          <span class="material-symbols-outlined" v-if="sortKey.name===attr && sortAsc">arrow_drop_up</span>
          <span class="material-symbols-outlined" v-else-if="sortKey.name===attr">arrow_drop_down</span>
        </div>
      </th>
      <th v-if="removable" class="red fw-semibold">
        Remove
      </th>
    </tr>
    </thead>
    <tbody>
    <tr v-for="item of sortedItems" :key="item.id" :class="{'activeTableRow': current === item, 'hoverTableRow': clickableRows}">
      <td v-for="attr of attributeValues" :key="attr" :class="{'clickable': clickableRows}" @click="emitRowClickEvent(item)">
        <span v-html="displayAttributeForItem(item, attr)" class="d-flex justify-content-center"></span>
      </td>
      <td v-if="removable" class="clickable" @click="emitRowRemoveEvent(item)">
        <span class="material-symbols-outlined d-flex justify-content-center red">delete</span>
      </td>
    </tr>
    </tbody>
  </table>
</template>

<script>
// @author leon
export default {
  name: "TableArrayComponent",
  data() {
    return {
      sortKey: null,
      sortAsc: true,
      current: null,
    };
  },
  props: {
    items: Array,
    attributeKeys: Object,
    attributeValues: Object,
    sortable: Boolean,
    clickableRows: Boolean,
    removable: Boolean,
  },
  created() {
    if (this.sortable && this.attributeValues) {
      this.sortKey = this.attributeValues[Object.keys(this.attributeValues)[0]];
    }
  },
  computed: {
    sortedItems() {
      let sorted = [...this.items];
      if (this.sortKey && this.sortable) {
        sorted.sort((a, b) => {
          if (typeof a[this.sortKey.name] === "number" && typeof b[this.sortKey.name] === "number") {
            return b[this.sortKey.name] - a[this.sortKey.name];
          }
          return this.displayAttributeForItem(b, this.sortKey).localeCompare(this.displayAttributeForItem(a, this.sortKey));
        });
        if (this.sortAsc) {
          sorted.reverse();
        }
      }
      return sorted;
    },
  },
  methods: {
    sort(key) {
      if (!this.sortable) {
        return;
      }

      if (key === this.sortKey.name) {
        this.sortAsc = !this.sortAsc;
      } else {
        this.sortKey = this.attributeValues[key];
        this.sortAsc = true;
      }
    },
    emitRowClickEvent(item) {
      if (this.clickableRows && item) {
        this.current = this.current !== item ? item : null;
        this.$emit('row-click', item);
      }
    },
    emitRowRemoveEvent(item) {
      if (this.removable && item) {
        this.$emit('row-remove', item);
      }
    },
    displayAttributeName(attributeName) {
      let expression = this.attributeKeys[attributeName];
      if (!expression) {
        return `${attributeName}`;
      }
      return `${expression(attributeName) ?? attributeName}`;
    },
    displayAttributeForItem(item, attribute) {
      let value = item[attribute.name];
      return `${value !== null && value !== undefined ? attribute(value) : null}`;
    }
  },
}
</script>

<style scoped>
.red {
  color: #a00;
}

.clickable {
  cursor: pointer;
}

table {
  border-collapse: collapse;
  margin: auto;
}

th, td {
  border: 1px solid #000;
  padding: 8px;
  height: 5vh;
  width: 15vw;
  text-align: center;
  justify-content: center;
}

thead {
  background-color: var(--color-highlight);
  color: var(--color-secondary);
}

tr:nth-child(even) {
  background-color: #222;
}
</style>