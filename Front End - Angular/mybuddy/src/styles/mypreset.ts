import { definePreset } from '@primeng/themes';
import Aura from '@primeng/themes/aura';

const MyBuddyPreset = definePreset(Aura, {
  semantic: {
    primary: {
      400: '@f0a346',
      500: '#ff7b0',
      600: '#cc6200',
    },
  },
});

export { MyBuddyPreset };
