package moe.aoramd.raindrop.adapter.list

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import moe.aoramd.raindrop.R
import moe.aoramd.raindrop.repository.RaindropRepository
import moe.aoramd.raindrop.view.music.MusicFragment
import moe.aoramd.raindrop.view.profile.ProfileFragment

class MainPagerAdapter(manager: FragmentManager) :
    FragmentPagerAdapter(manager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    private val fragments: List<Fragment> = listOf(
        MusicFragment(), ProfileFragment()
    )

    private val fragmentTitles: List<Int> = listOf(
        R.string.main_music, R.string.main_profile
    )

    override fun getItem(position: Int): Fragment {
        return fragments[position]
    }

    override fun getCount(): Int {
        return fragments.size
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return RaindropRepository.resourceString(fragmentTitles[position])
    }
}