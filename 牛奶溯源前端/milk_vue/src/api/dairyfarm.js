import request from '@/utils/request'

export function fetchList(query) {
  return request({
    url: '/dairyfarm/list',
    method: 'get',
    params: query
  })
}

export function createDairyFarm(data) {
  return request({
    url: '/dairyfarm/create',
    method: 'post',
    data
  })
}

export function updateUser(data) {
  return request({
    url: '/admin/update',
    method: 'post',
    data
  })
}

export function fetchPv(pv) {
  return request({
    url: '/vue-element-admin/article/pv',
    method: 'get',
    params: { pv }
  })
}


export function deleteUser(data) {
  return request({
    url: '/admin/delete',
    method: 'post',
    data
  })
}


