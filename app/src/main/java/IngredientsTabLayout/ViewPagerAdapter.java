package IngredientsTabLayout;

import static androidx.fragment.app.FragmentStatePagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;
;import BottomNavigationBar.Generate;

public class ViewPagerAdapter extends FragmentStateAdapter {

    public ViewPagerAdapter(@NonNull Generate fragmentActivity) {
        super(fragmentActivity);
    }


    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch(position){
            case 0:
                return new Pork();
            case 1:
                return new Chicken();
            case 2:
                return new Beef();
            case 3:
                return new Fish();
            case 4:
                return new Vegetables();
            case 5:
                return new Condiments();
            case 6:
                return new Sauces();
            case 7:
                return new Fruits();
            case 8:
                return new Pantry();
            default:
                return new Pork();
        }
    }

    @Override
    public int getItemCount() {
        return 9;
    }
}
