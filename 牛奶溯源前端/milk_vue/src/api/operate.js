import request from '@/utils/request'

export function operateData(data) {
  return request({
    url: '/dairyfarm/operate',
    method: 'post',
    data
  })
}

