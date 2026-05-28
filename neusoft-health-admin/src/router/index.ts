import { createRouter, createWebHistory } from 'vue-router'
import type { RouteRecordRaw } from 'vue-router'
import { useAuthStore } from '@/stores/auth'

const AdminLayout = () => import('@/layouts/AdminLayout.vue')

const routes: RouteRecordRaw[] = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/login/index.vue'),
    meta: { title: '登录', requiresAuth: false }
  },
  {
    path: '/screen',
    name: 'DataScreen',
    component: () => import('@/views/screen/index.vue'),
    meta: { title: '数据大屏', requiresAuth: true, roles: ['R002', 'R003'] }
  },
  {
    path: '/',
    component: AdminLayout,
    redirect: '/dashboard',
    children: [
      {
        path: 'dashboard',
        name: 'Dashboard',
        component: () => import('@/views/dashboard/index.vue'),
        meta: { title: '工作台', icon: 'Monitor' }
      },
      {
        path: 'users',
        name: 'Users',
        component: () => import('@/views/user/index.vue'),
        meta: { title: '用户管理', icon: 'User', roles: ['R002', 'R003'] }
      },
      {
        path: 'review',
        name: 'Review',
        component: () => import('@/views/review/index.vue'),
        meta: { title: '内容审核', icon: 'Document', roles: ['R002', 'R004'] }
      },
      {
        path: 'members',
        name: 'Members',
        component: () => import('@/views/member/index.vue'),
        meta: { title: '会员管理', icon: 'UserFilled', roles: ['R002', 'R003'] }
      },
      {
        path: 'orders',
        name: 'Orders',
        component: () => import('@/views/payment/orders.vue'),
        meta: { title: '订单管理', icon: 'ShoppingCart', roles: ['R002', 'R003'] }
      },
      {
        path: 'content',
        name: 'Content',
        redirect: '/content/faq',
        meta: { title: '内容管理', icon: 'Files', roles: ['R002', 'R004'] },
        children: [
          {
            path: 'faq',
            name: 'FAQ',
            component: () => import('@/views/content/faq.vue'),
            meta: { title: '常见问题', roles: ['R002', 'R004'] }
          },
          {
            path: 'sensitive-words',
            name: 'SensitiveWords',
            component: () => import('@/views/content/sensitive-words.vue'),
            meta: { title: '敏感词管理', roles: ['R002', 'R004'] }
          }
        ]
      },
      {
        path: 'config',
        name: 'Config',
        component: () => import('@/views/config/index.vue'),
        meta: { title: '系统配置', icon: 'Setting', roles: ['R002', 'R003'] }
      },
      {
        path: 'permission',
        name: 'Permission',
        redirect: '/permission/roles',
        meta: { title: '权限管理', icon: 'Lock', roles: ['R002', 'R003'] },
        children: [
          {
            path: 'roles',
            name: 'Roles',
            component: () => import('@/views/permission/roles.vue'),
            meta: { title: '角色管理', roles: ['R002', 'R003'] }
          },
          {
            path: 'permissions',
            name: 'Permissions',
            component: () => import('@/views/permission/permissions.vue'),
            meta: { title: '权限配置', roles: ['R002', 'R003'] }
          }
        ]
      },
      {
        path: 'logs',
        name: 'Logs',
        component: () => import('@/views/log/index.vue'),
        meta: { title: '操作日志', icon: 'Notebook', roles: ['R002', 'R003'] }
      }
    ]
  },
  {
    path: '/:pathMatch(.*)*',
    redirect: '/dashboard'
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach(async (to, from, next) => {
  const authStore = useAuthStore()
  const requiresAuth = to.meta.requiresAuth !== false

  if (to.path === '/login') {
    if (authStore.token) {
      next({ path: '/' })
    } else {
      next()
    }
    return
  }

  if (requiresAuth && !authStore.token) {
    next({ path: '/login', query: { redirect: to.path } })
    return
  }

  if (authStore.token && !authStore.user) {
    try {
      await authStore.getUserInfo()
    } catch {
      authStore.logout()
      next({ path: '/login', query: { redirect: to.path } })
      return
    }
  }

  const requiredRoles = to.meta.roles as string[] | undefined
  if (requiredRoles && requiredRoles.length > 0) {
    if (!authStore.hasAnyRole(requiredRoles)) {
      next({ path: '/dashboard' })
      return
    }
  }

  next()
})

export default router
