package com.wuxing.adapter;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.TextView;

import com.amap.api.services.core.PoiItem;
import com.amap.api.services.poisearch.SubPoiItem;
import com.wuxing.activity.R;

public class PoiListAdapter extends BaseAdapter{
    private Context ctx;
    private List<PoiItem> list;

    public PoiListAdapter(Context context, List<PoiItem> poiList) {
        this.ctx = context;
        this.list = poiList;
    }
    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = View.inflate(ctx, R.layout.poi_reslut, null);
            holder.tv_result = (TextView) convertView
                   .findViewById(R.id.tv_result);
//            holder.subpois = (GridView) convertView.findViewById(R.id.listview_item_gridview);
            holder.tv_address = (TextView) convertView.findViewById(R.id.tv_address);
            holder.tv_distence = (TextView) convertView.findViewById(R.id.tv_distence);
            holder.tv_des =(TextView) convertView.findViewById(R.id.tv_des);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        PoiItem item = list.get(position);
        holder.tv_result.setText(item.getTitle());
        holder.tv_address.setText(item.getAdName()==null?"":item.getAdName());
        holder.tv_des.setText(item.getTypeDes()==null?"":item.getTypeDes());
        holder.tv_distence.setText(item.getDistance()==-1?0+"":item.getDistance()+"米");
//        if (item.getSubPois().size() > 0) {
//            List<SubPoiItem> subPoiItems = item.getSubPois();
//            SubPoiAdapter subpoiAdapter=new SubPoiAdapter(ctx, subPoiItems); 
//            holder.subpois.setAdapter(subpoiAdapter); 
//        }


        return convertView;
    }
    private class ViewHolder {
        TextView tv_result,tv_address,tv_distence,tv_des;
        //GridView subpois;
    }

}
