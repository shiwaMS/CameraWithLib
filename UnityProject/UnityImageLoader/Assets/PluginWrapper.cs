using System.Collections;
using System.Collections.Generic;
using UnityEngine;

public class PluginWrapper : MonoBehaviour {

	// Use this for initialization
	void Start () {
        TextMesh textMesh = GetComponent<TextMesh>();

        AndroidJavaClass jc = new AndroidJavaClass("com.unity3d.player.UnityPlayer"); 
        AndroidJavaObject jo = jc.GetStatic<AndroidJavaObject>("currentActivity");

        var plugin = new AndroidJavaClass("microsoft.prototype.braodcastsupport.ImageLoader");
        //textMesh.text = plugin.CallStatic<string>("loadTextureIdStr", "/storage/emulated/0/myscreen_7.png");
        textMesh.text = plugin.CallStatic<string>("setContext", jo);
	}
}
