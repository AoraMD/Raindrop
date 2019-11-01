package moe.aoramd.raindrop.view.main

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import moe.aoramd.lookinglass.manager.ContextManager
import moe.aoramd.raindrop.R
import moe.aoramd.raindrop.view.music.MusicFragment
import moe.aoramd.raindrop.view.profile.ProfileFragment

/**
 *  main interface fragment view pager adapter
 *
 *  @constructor
 *  create new instance
 *
 *  @param manager support fragment manager
 *
 *  @author M.D
 *  @version dev 1
 */
class MainPagerAdapter(manager: FragmentManager) :
    FragmentPagerAdapter(manager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    private val fragments: List<Fragment> = listOf(
        MusicFragment(), ProfileFragment()
    )

    private val fragmentTitles: List<Int> = listOf(
        R.string.music, R.string.profile
    )

    override fun getItem(position: Int): Fragment = fragments[position]

    override fun getCount(): Int = fragments.size

    override fun getPageTitle(position: Int): CharSequence? =
        ContextManager.resourceString(fragmentTitles[position])
}