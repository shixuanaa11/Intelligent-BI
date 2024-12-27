// @ts-ignore
/* eslint-disable */
import request from '@/utils/myaxios'

/** 此处后端没有提供注释 GET /queue/add */
export async function add(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.addParams,
  options?: { [key: string]: any }
) {
  return request<any>('/queue/add', {
    method: 'GET',
    params: {
      ...params,
    },
    ...(options || {}),
  })
}

/** 此处后端没有提供注释 GET /queue/get */
export async function get(options?: { [key: string]: any }) {
  return request<string>('/queue/get', {
    method: 'GET',
    ...(options || {}),
  })
}
