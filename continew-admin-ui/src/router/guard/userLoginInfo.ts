import type { Router, LocationQueryRaw } from 'vue-router';
import NProgress from 'nprogress'; // progress bar

import { useLoginStore, useAppStore } from '@/store';
import { isLogin } from '@/utils/auth';

export default function setupUserLoginInfoGuard(router: Router) {
  router.beforeEach(async (to, from, next) => {
    NProgress.start();
    const loginStore = useLoginStore();
    const appStore = useAppStore();
    appStore.init();
    if (isLogin()) {
      if (loginStore.roles[0]) {
        next();
      } else {
        try {
          await loginStore.getInfo();
          next();
        } catch (error) {
          await loginStore.logout();
          next({
            name: 'login',
            query: {
              redirect: to.name,
              ...to.query,
            } as LocationQueryRaw,
          });
        }
      }
    } else {
      if (to.name === 'login' || to.name === 'SocialCallback') {
        next();
        return;
      }
      next({
        name: 'login',
        query: {
          redirect: to.name,
          ...to.query,
        } as LocationQueryRaw,
      });
    }
  });
}
