using System;
using System.Collections;
using System.Collections.Generic;
using System.IO;
using UnityEngine;
using UnityEngine.UI;

public class PluginWrapper : MonoBehaviour
{
    public RawImage display;
    public AspectRatioFitter fit;


    // Use this for initialization
    void Start()
    {
        //TextMesh textMesh = GetComponent<TextMesh>();

        //AndroidJavaClass jc = new AndroidJavaClass("com.unity3d.player.UnityPlayer"); 
        //AndroidJavaObject jo = jc.GetStatic<AndroidJavaObject>("currentActivity");

        //var plugin = new AndroidJavaClass("microsoft.prototype.broadcastsupport.ImageLoader");
        //textMesh.text = plugin.CallStatic<string>("loadTextureIdStr", jo, "/storage/emulated/0/myscreen_7.png");
        //textMesh.text = plugin.CallStatic<string>("setContext", jo);

        Texture2D texture2D = new Texture2D(540, 960, TextureFormat.ARGB32, false);

        var plugin = new AndroidJavaClass("microsoft.prototype.broadcastsupport.ImageLoader");
        Int32 texturePtr = plugin.CallStatic<Int32>("loadTextureId", "/storage/emulated/0/myscreen_7.png");
        Debug.Log("texture pointer = " + texturePtr);

        //if (texturePtr > 0)
        //{
        //    Texture2D nativeTexture = Texture2D.CreateExternalTexture(540, 960, TextureFormat.ARGB32, false, false, (IntPtr)texturePtr);
        //    if (nativeTexture == null)
        //    {
        //        Debug.Log("nativeTexture object is null ");
        //        return;
        //    }
        //    else
        //    {
        //        Debug.Log("nativeTexture pixel length: " +
        //                nativeTexture.GetPixels().Length);
        //        texture2D.UpdateExternalTexture(nativeTexture.GetNativeTexturePtr());
        //        display.texture = texture2D;
        //    }

        //    float ratio = 540.0f / 960.0f;
        //    fit.aspectRatio = ratio;
        //}

        byte[] fileData = File.ReadAllBytes("/storage/emulated/0/myscreen_7.png");
        Texture2D tex = new Texture2D(2, 2);
        tex.LoadImage(fileData);

        float ratio = (float)tex.width / (float)tex.height;
        fit.aspectRatio = ratio;
        Debug.Log("texture ratio = " + ratio);

        display.texture = tex;
    }


    void Update()
    { }

}
