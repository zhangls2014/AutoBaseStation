package com.zhangls.auto.view

import android.arch.lifecycle.Observer
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.zhangls.auto.AbstractDatabase
import com.zhangls.auto.R
import com.zhangls.auto.model.*
import com.zhangls.auto.type.*
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_item_list.*
import me.drakeet.multitype.Items
import me.drakeet.multitype.MultiTypeAdapter

/**
 * 列表 Item
 *
 * @author zhangls
 */
class ItemFragment : Fragment() {

    private val items = Items()
    private val adapter = MultiTypeAdapter(items)
    private lateinit var database: AbstractDatabase


    companion object {

        private const val TYPE_STRING = "type_string"
        const val ACTION_GSM = 1
        const val ACTION_LTE = 2
        const val ACTION_CDMA = 3
        const val ACTION_WCDMA = 4
        const val ACTION_LOCATION = 5

        fun newInstance(type: Int): ItemFragment {
            val fragment = ItemFragment()
            val args = Bundle()
            args.putInt(TYPE_STRING, type)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater!!.inflate(R.layout.fragment_item_list, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recycler.layoutManager = LinearLayoutManager(context)
        adapter.register(CellInfoGsmModel::class.java, GsmViewBinder(fragmentManager))
        adapter.register(CellInfoLteModel::class.java, LteViewBinder(fragmentManager))
        adapter.register(CellInfoCdmaModel::class.java, CdmaViewBinder(fragmentManager))
        adapter.register(CellInfoWcdmaModel::class.java, WcdmaViewBinder(fragmentManager))
        adapter.register(LocationModel::class.java, LocationViewBinder())
        recycler.adapter = adapter

        refreshData()
    }

    /**
     * 刷新数据
     */
    private fun refreshData() {
        Observable.create<Any> { it ->
            database = AbstractDatabase.get(context)
            when {
                arguments.getInt(TYPE_STRING) == ACTION_GSM -> {
                    items.addAll(database.gsmDao().getAllGsm())

                    // 观察数据，每次数据变化时刷新列表数据
                    database.gsmDao().getAllGsmData().observe(this, Observer {
                        items.clear()
                        adapter.notifyDataSetChanged()
                        if (it != null) {
                            items.addAll(it)
                            adapter.notifyDataSetChanged()
                        }
                    })
                }
                arguments.getInt(TYPE_STRING) == ACTION_LTE -> {
                    items.addAll(database.lteDao().getAllLte())

                    // 观察数据，每次数据变化时刷新列表数据
                    database.lteDao().getAllLteData().observe(this, Observer {
                        items.clear()
                        adapter.notifyDataSetChanged()
                        if (it != null) {
                            items.addAll(it)
                            adapter.notifyDataSetChanged()
                        }
                    })
                }
                arguments.getInt(TYPE_STRING) == ACTION_CDMA -> {
                    items.addAll(database.cdmaDao().getAllCdma())

                    // 观察数据，每次数据变化时刷新列表数据
                    database.cdmaDao().getAllCdmaData().observe(this, Observer {
                        items.clear()
                        adapter.notifyDataSetChanged()
                        if (it != null) {
                            items.addAll(it)
                            adapter.notifyDataSetChanged()
                        }
                    })
                }
                arguments.getInt(TYPE_STRING) == ACTION_WCDMA -> {
                    items.addAll(database.wcdmaDao().getAllWcdma())

                    // 观察数据，每次数据变化时刷新列表数据
                    database.wcdmaDao().getAllWcdmaData().observe(this, Observer {
                        items.clear()
                        adapter.notifyDataSetChanged()
                        if (it != null) {
                            items.addAll(it)
                            adapter.notifyDataSetChanged()
                        }
                    })
                }
                else -> {
                    items.addAll(database.locationDao().getAllLocation())

                    // 观察数据，每次数据变化时刷新列表数据
                    database.locationDao().getAllLocationData().observe(this, Observer {
                        items.clear()
                        adapter.notifyDataSetChanged()
                        if (it != null) {
                            items.addAll(it)
                            adapter.notifyDataSetChanged()
                        }
                    })
                }
            }
        }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { adapter.notifyDataSetChanged() }
    }
}
