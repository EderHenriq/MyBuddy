import { definePreset } from '@primeng/themes';
import Aura from '@primeng/themes/aura';

const MyBuddyPreset = definePreset(Aura, {
  semantic: {
    primary: {
      50: '#fff3e0',
      100: '#ffe0b2',
      200: '#ffcc80',
      300: '#ffb74d',
      400: '#f0a346',
      500: '#ff7b00',
      600: '#cc6200',
      700: '#994a00',
      800: '#663100',
      900: '#331900',
      950: '#1a0d00',
    },
    colorScheme: {
      light: {
        primary: {
          color: '{primary.500}',
          contrastColor: '#ffffff',
          hoverColor: '{primary.600}',
          activeColor: '{primary.700}',
        },
      },
      dark: {
        primary: {
          color: '{primary.500}',
          contrastColor: '#3a3737',
          hoverColor: '{primary.600}',
          activeColor: '{primary.700}',
        },
      },
    },
  },
  components: {
    inputtext: {
      root: {
        borderRadius: '8px',
        borderColor: '#d9d9d9',
        focusBorderColor: '#2d2d2d',
      },
    },
    checkbox: {
      root: {
        checkedBackground: '#5d831c',
        checkedBorderColor: '#5d831c',
        focusBorderColor: '#5d831c',
        checkedHoverBackground: '#4a6b16',
        checkedHoverBorderColor: '#4a6b16',
        focusRing: {
          color: '#5d831c',
        },
      },
      icon: {
        checkedColor: '#ffffff',
      },
    },
  },
});

export { MyBuddyPreset };
