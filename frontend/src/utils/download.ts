import { ElMessage } from 'element-plus'

// 下载文件
export async function downloadFile(url: string, filename: string) {
  try {
    const response = await fetch(import.meta.env.VITE_API_BASE_URL + url, {
      credentials: 'include'
    })
    if (!response.ok) {
      throw new Error('下载失败')
    }
    const blob = await response.blob()
    const link = document.createElement('a')
    link.href = URL.createObjectURL(blob)
    link.download = filename
    link.click()
    URL.revokeObjectURL(link.href)
  } catch (error) {
    ElMessage.error('下载失败')
    throw error
  }
}

// 导出 Excel
export function exportExcel(id: string, filename: string = '培养计划.xlsx') {
  return downloadFile(`/api/training-program/export/${id}`, filename)
}

// 下载模板
export function downloadTemplate(filename: string = '培养计划模板.xlsx') {
  return downloadFile('/api/training-program/template', filename)
}
