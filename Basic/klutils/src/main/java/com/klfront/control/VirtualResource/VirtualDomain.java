package com.klfront.control.VirtualResource;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dell on 2017/9/15.
 */

public class VirtualDomain {
    public String Name;
    public List<String> Regex = new ArrayList<>();
    public List<VirtualResource> Items = new ArrayList<>();
}
