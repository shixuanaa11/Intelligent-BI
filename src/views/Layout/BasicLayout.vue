<template>
  <a-layout>
    <a-layout-header :style="{ width: '100%', background: '#fff' }">
      <a-row :wrap="false">
        <a-col flex="150px">
          <div class="logo">
            <img src="@/assets/logo.png" class="img" alt="" />
            <div class="title">智能BI</div>
          </div></a-col
        >
        <a-col flex="auto"
          ><a-menu
            v-model:selectedKeys="selectedKeys"
            mode="horizontal"
            :style="{ lineHeight: '64px' }"
            :items="filterMenusResult"
            @click="doMenuClick"
          >
            <!-- <a-menu-item key="/home" @click="changeMenu('home')">
              <span>主页</span>
            </a-menu-item>
            <a-menu-item key="/add_picture" @click="changeMenu('add_picture')">
              <span>创建图片</span>
            </a-menu-item>
            <a-menu-item key="/chart" @click="changeMenu('chart')">
              <span>智能分析</span>
            </a-menu-item>
            <a-menu-item key="/chart/Async" @click="changeMenu('chartAsync')">
              <span>异步分析</span>
            </a-menu-item>
            <a-menu-item key="/mychart" @click="changeMenu('mychart')">
              <span>图表管理</span>
            </a-menu-item>
            <a-menu-item key="/admin/userManager" @click="changeMenu('admin/userManager')">
              <span>用户管理</span>
            </a-menu-item>
            <a-menu-item key="/my" @click="changeMenu('my')">
              <span>我的</span>
            </a-menu-item> -->
          </a-menu></a-col
        >
        <a-col flex="120px">
          <a-button type="primary" v-if="!userStore.userInfo?.id" @click="tologin">登录</a-button>
          <a-dropdown :arrow="{ pointAtCenter: true }" placement="bottom" v-else>
            <a class="ant-dropdown-link" @click.prevent>
              <a-avatar :src="avatar()" style="margin-right: 8px" />
              <span>
                {{
                  userStore.userInfo.username
                    ? userStore.userInfo.username
                    : userStore.userInfo.account
                }}
              </span>
            </a>
            <template #overlay>
              <a-menu>
                <a-menu-item>
                  <a href="javascript:;">个人主页</a>
                </a-menu-item>
                <!-- <a-menu-item>
                  <a href="javascript:;">2nd menu item</a>
                </a-menu-item> -->
                <a-menu-divider />
                <a-menu-item @click="logout">
                  <a href="javascript:;">退出登录</a>
                </a-menu-item>
              </a-menu>
            </template>
          </a-dropdown>
        </a-col>
      </a-row>
    </a-layout-header>
    <a-layout-content :style="{ padding: '0 50px' }">
      <!-- <a-breadcrumb :style="{ margin: '16px 0' }">
        <a-breadcrumb-item>Home</a-breadcrumb-item>
        <a-breadcrumb-item>List</a-breadcrumb-item>
        <a-breadcrumb-item>App</a-breadcrumb-item>
      </a-breadcrumb> -->
      <div :style="{ margin: '16px 0' }"></div>
      <div :style="{ background: '#fff', padding: '24px', minHeight: '530px' }">
        <router-view></router-view>
      </div>
    </a-layout-content>
    <a-layout-footer :style="{ textAlign: 'center' }">
      Intelligent BI ©2024 Created by axuan
    </a-layout-footer>
  </a-layout>
</template>
<script setup>
import { computed, onMounted, ref } from 'vue'
const selectedKeys = ref([])
// 路由
import { useRouter } from 'vue-router'
const router = useRouter()
// const route = useRoute()
// 路由变量
// import { routes } from '@/router/routes'
// const menuRouter = ref([routes[0].children])
// 菜单切换
// const aa = ({ key }) => {
//   console.log('菜单切换', key)
// }
// 接口
import { UserlLogout } from '@/myapi/user'
// pinia
import { useUserStore } from '@/stores/user'
import { message } from 'ant-design-vue'
import ACCESS_ENUM from '@/access/accessEnum'
const userStore = useUserStore()

// 菜单(这里我们规定key以admin开头的菜单为管理员菜单，普通用户登录是看不到的)
const items = ref([
  {
    key: 'home',
    // icon: () => h(MailOutlined),
    label: '主页',
    title: '主页',
  },
  {
    key: 'add_picture',
    label: '创建图片',
    title: '创建图片',
  },
  {
    key: 'chart',
    label: '智能分析',
    title: '智能分析',
  },
  {
    key: 'chartAsync',
    label: '异步分析',
    title: '异步分析',
  },
  {
    key: 'mychart',
    // icon: () => h(MailOutlined),
    label: '图表管理',
    title: '图表管理',
  },
  {
    key: 'adminPictureManager',
    label: '图片管理',
    title: '图片管理',
  },
  {
    key: 'adminUserManager',
    // icon: () => h(MailOutlined),
    label: '用户管理',
    title: '用户管理',
  },
  {
    key: 'my',
    // icon: () => h(MailOutlined),
    label: '我的',
    title: '我的',
  },
])

// 切换菜单
const doMenuClick = ({ key }) => {
  router.push({ name: key })
}
// 路由更新时自动更新选中的菜单项
router.afterEach((to) => {
  console.log('菜单更新', to)

  selectedKeys.value = [to.name]
})
// 根据权限过滤菜单项
const filterMenus = (menus) => {
  return menus?.filter((menu) => {
    // 如果菜单以admin开头
    if (menu.key.startsWith('admin')) {
      // 获取pinia里的用户信息
      // 如果用户信息不存在,或者存在了但userRole不是管理员,就返回false,不显示这个菜单
      const loginUser = useUserStore().userInfo
      if (!loginUser || loginUser.userRole !== 'admin') {
        return false
      }
    }
    return true
  })
}
// 写完方法，我们来引用，过滤原始的菜单项(有变化都要重新过滤一次用computed)
const filterMenusResult = computed(() => {
  return filterMenus(items.value)
})
// 跳转登录页
const tologin = () => {
  router.push({ name: 'login' })
}
// 注销 退出登录
const logout = async () => {
  const res = await UserlLogout()
  if (res.code === 0) {
    message.success('注销成功')
    // 清空用户信息
    userStore.SET_USERINFO({ userRole: ACCESS_ENUM.NOT_LOGIN })
    router.push({ name: 'login' })
  } else {
    message.error('退出登录失败')
  }
}
// 头像
const avatar = () => {
  return userStore.userInfo.avatarUrl
    ? userStore.userInfo.avatarUrl
    : 'https://zos.alipayobjects.com/rmsportal/jkjgkEfvpUPVyRjUImniVslZfWPnJuuZ.png'
}
</script>
<style scoped>
.site-layout .site-layout-background {
  background: #fff;
}

[data-theme='dark'] .site-layout .site-layout-background {
  background: #141414;
}
.logo {
  display: flex;
  align-items: center;
  text-align: center;
  width: 100px;
  /* justify-content: center; */
  .img {
    height: 36px;
  }
  .title {
    margin-left: 10px;
  }
}
</style>
