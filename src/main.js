// import './assets/main.css'

import { createApp } from 'vue'
import { createPinia } from 'pinia'
import Antd from 'ant-design-vue'
import App from './App.vue'
import 'ant-design-vue/dist/reset.css'
// 引入echarts
import Echarts from 'vue-echarts'
import * as echarts from 'echarts'
import router from './router'

import './global.less'
import '@/router/permission'
const app = createApp(App)
// 使用组件
app.component('e-charts', Echarts)
app.use(createPinia())
app.use(router)
app.use(Antd)
app.mount('#app')
