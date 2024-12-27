<template>
  <div id="user-manager-page">
    <a-form layout="inline" :model="searchParams" @finish="doSearch">
      <a-form-item label="账号">
        <a-input v-model:value="searchParams.account" placeholder="输入账号" allow-clear />
      </a-form-item>
      <a-form-item label="用户名">
        <a-input v-model:value="searchParams.username" placeholder="输入用户名" allow-clear />
      </a-form-item>
      <a-form-item>
        <a-button type="primary" html-type="submit">搜索</a-button>
      </a-form-item>
    </a-form>
    <div style="margin-bottom: 16px"></div>
    <a-table :columns="columns" :data-source="dataList" :pagination="pagination">
      <template #bodyCell="{ column, record }">
        <template v-if="column.dataIndex === 'avatarUrl'">
          <a-image :src="record.avatarUrl" :width="120" />
        </template>
        <template v-if="column.dataIndex === 'username'">
          {{ record.username ? record.username : '未命名' }}
        </template>
        <template v-else-if="column.dataIndex === 'userRole'">
          <div v-if="record.userRole === 'admin'">
            <a-tag color="green">管理员</a-tag>
          </div>
          <div v-else>
            <a-tag color="blue">普通用户</a-tag>
          </div>
        </template>
        <template v-else-if="column.dataIndex === 'createTime'">
          {{ dayjs(record.createTime).format('YYYY-MM-DD HH:mm:ss') }}
        </template>
        <template v-else-if="column.dataIndex === 'updateTime'">
          {{ dayjs(record.updateTime).format('YYYY-MM-DD HH:mm:ss') }}
        </template>
        <template v-else-if="column.key === 'action'">
          <a-space wrap>
            <a-button type="default">编辑</a-button>
            <a-button danger @click="deleteUserById(record)">删除</a-button>
          </a-space>
        </template>
      </template>
    </a-table>
  </div>
</template>
<script setup>
import { deleteUser, listUserVOByPage } from '@/myapi/user'
import { message, Modal } from 'ant-design-vue'
import { computed, onMounted, reactive, ref } from 'vue'
// 组件库自带
import dayjs from 'dayjs'

const columns = [
  {
    title: 'id',
    dataIndex: 'id',
  },
  {
    title: '账号',
    dataIndex: 'account',
  },
  {
    title: '用户名',
    dataIndex: 'username',
  },
  {
    title: '头像',
    dataIndex: 'avatarUrl',
  },
  {
    title: '简介',
    dataIndex: 'userProfile',
  },
  {
    title: '用户角色',
    dataIndex: 'userRole',
  },
  {
    title: '创建时间',
    dataIndex: 'createTime',
  },
  {
    title: '更新时间',
    dataIndex: 'updateTime',
  },
  {
    title: '操作',
    key: 'action',
  },
]

const dataList = ref([])
const total = ref()
const searchParams = reactive({
  current: 1,
  pageSize: 10,
})
const fetchData = async () => {
  const res = await listUserVOByPage({ ...searchParams })
  if (res.code == 0) {
    dataList.value = res.data.records ?? []
    total.value = res.data.total ?? 0
  } else {
    message.error('获取数据失败')
  }
}
onMounted(() => {
  fetchData()
})
// 删除
const deleteUserById = async (record) => {
  Modal.confirm({
    title: '删除图片',
    // icon: h(ExclamationCircleOutlined),
    content: '你确定要删除该用户?',
    okText: '确认',
    cancelText: '取消',
    async onOk() {
      try {
        const res = await deleteUser({ id: record.id })
        if (res.code == 0) {
          message.success('删除成功')
          fetchData()
        } else {
          message.error('删除失败')
        }
      } catch {
        return console.log('Oops errors!')
      }
    },
    onCancel() {},
  })
}
// 分页参数
const pagination = computed(() => {
  return {
    current: searchParams.current ?? 1,
    pageSize: searchParams.pageSize ?? 10,
    total: total.value,
    showSizeChanger: true,
    showTotal: (total) => `共 ${total} 条`,
    onChange: (page, pageSize) => {
      searchParams.current = page
      searchParams.pageSize = pageSize
      fetchData()
    },
  }
})
// 搜索（点击按钮触发）
const doSearch = () => {
  // 重置页码（一定要加）
  searchParams.current = 1
  fetchData()
}
</script>
