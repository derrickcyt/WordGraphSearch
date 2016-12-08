package derrick.ltp.method;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

import ltpTools.LTPDATA;
import ltpTools.Word;

public interface IParser extends Remote {

	// public LTPDATA doLtp(String text) throws RemoteException;
	public List<LTPDATA> doLtp(String text) throws RemoteException;

	public List<Word> dParseAnalyse(String text) throws RemoteException;

	public List<Word> segmentation(String text) throws RemoteException;

}
