import { reactive, ref } from 'vue'
import { defineStore } from 'pinia'

export const useChartStore = defineStore('chart', () => {
  const AIGENRESULT = ref('')
  const AIGENCHART = ref({})
  const SET_AIGENRESULT = (data) => {
    AIGENRESULT.value = data
  }
  const SET_AIGENCHART = (data) => {
    AIGENCHART.value = data
    console.log('AIGENCHART', data)
  }
  return {
    AIGENRESULT,
    SET_AIGENRESULT,
    AIGENCHART,
    SET_AIGENCHART,
  }
})
