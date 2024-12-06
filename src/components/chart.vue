<template>
  <v-chart class="chart" :option="option" autoresize />
</template>

<script setup>
import VChart from 'vue-echarts'
import { computed, watchEffect } from 'vue'
// pinia
import { useChartStore } from '@/stores/chart'
import { storeToRefs } from 'pinia'

const { AIGENCHART: chartStore } = storeToRefs(useChartStore())

const option = computed(() => {
  return chartStore.value
})
watchEffect(() => {
  chartStore
})
</script>

<style scoped>
.chart {
  /* height: 100vh; */
  /* width: 400px; */
  height: 300px;
}
</style>
