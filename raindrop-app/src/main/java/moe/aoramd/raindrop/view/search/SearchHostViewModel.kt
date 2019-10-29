package moe.aoramd.raindrop.view.search

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import moe.aoramd.raindrop.view.base.control.BarControlViewModel

class SearchHostViewModel : BarControlViewModel() {

    private val _submitted = MutableLiveData<Boolean>().apply { value = false }
    val submitted: LiveData<Boolean> = _submitted

    internal fun submit() {
        _submitted.value = true
    }
}