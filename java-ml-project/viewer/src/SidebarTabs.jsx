import './SidebarTabs.css'

export default function SidebarTabs({ activeTab, onTabChange }) {
  return (
    <div className="sidebar-tabs">
      <button
        type="button"
        className={`sidebar-tab ${activeTab === 'steps' ? 'active' : ''}`}
        onClick={() => onTabChange('steps')}
      >
        Steps
      </button>
      <button
        type="button"
        className={`sidebar-tab ${activeTab === 'codeExplorer' ? 'active' : ''}`}
        onClick={() => onTabChange('codeExplorer')}
      >
        Code Explorer
      </button>
    </div>
  )
}
